pipeline {
    agent any
    parameters {
        choice(name: 'ACTION', choices: ['apply', 'destroy'], description: 'Select Terraform action')
    }
    environment {
        ANSIBLE_HOSTS_FILE = 'inventory/hosts.yml'
        AWS_ACCESS_KEY_ID = ''
        AWS_SECRET_ACCESS_KEY = ''
    }
    stages {
        stage('Checkout') {
            steps {
                git(
                    url: 'https://github.com/dariusbrinzan/License_Config.git',
                    branch: 'master',
                    credentialsId: ''
                )
            }
        }
        stage('Terraform Init & Apply/Destroy') {
            steps {
                dir('Terraform') {
                    script {
                        if (params.ACTION == 'apply') {
                            sh('terraform init')
                            sh('terraform plan')
                            sh('terraform apply -auto-approve')
                        } else {
                            sh('terraform state list')
                            sh('terraform destroy -auto-approve')
                        }
                    }
                }
            }
        }
         stage('Extract IPs and Update Ansible Inventory') {
            when {
                expression { params.ACTION == 'apply' }
            }
            steps {
                script {
                    dir('Terraform') {
                        def ips = sh(script: "terraform output -json instance_public_ips | jq -r '.[]'", returnStdout: true).trim()
                        def servers = ""
                        ips.tokenize('\n').eachWithIndex { ip, idx ->
                            servers += "    server${idx + 1}:\n      ansible_host: ${ip}\n"
                        }
                        def inventory = "all:\n  hosts:\n${servers}"
                        print(inventory)
                        print(env.ANSIBLE_HOSTS_FILE)
                        sh('cat ${ANSIBLE_HOSTS_FILE}')
                        sh('chmod 777 ../Ansible/inventory/hosts.yml')
                        sh('chmod 400 access.pem')
                        writeFile(file: "hosts.yml", text: inventory)
                        sh('cp hosts.yml ../Ansible/inventory/hosts.yml')
                        sh('cat ../Ansible/inventory/hosts.yml')
                    }
                }
            }
        }
        stage('Wait for EC2 Instances') {
            when {
                expression { params.ACTION == 'apply' }
            }
            steps {
                script {
                    echo "Waiting for EC2 instances to initialize..."
                    sleep 20
                }
            }
        }
        stage('Run Ansible Playbooks') {
            when {
                expression { params.ACTION == 'apply' }
            }
            steps {
                dir('Ansible') {
                    sh('ls -lh')
                    sh('cat inventory/hosts.yml')
                    sh("ansible-playbook -i inventory/hosts.yml main_playbook.yml")
                }
            }
        }
    }
}
