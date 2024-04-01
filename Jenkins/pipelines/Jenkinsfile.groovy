pipeline {
    agent any
    parameters {
        choice(name: 'ACTION', choices: ['apply', 'destroy'], description: 'Select Terraform action')
    }
    environment {
        ANSIBLE_HOSTS_FILE = 'inventory/hosts.yml'
        AWS_ACCESS_KEY_ID = 'AKIAXYKJWHSHNVBLCUNO'
        AWS_SECRET_ACCESS_KEY = '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'
    }
    stages {
        stage('Checkout') {
            steps {
                git(
                    url: 'https://github.com/dariusbrinzan/License_Config.git',
                    branch: 'master',
                    credentialsId: '8ae6862b-0828-40ea-ab30-4c43adc486b4'
                )
            }
        }
        stage('Terraform Init & Apply/Destroy') {
            steps {
                print(sh('ls -la'))
                dir ('Terraform') {
                    script {
                        print(sh('ls -la'))
                        print(sh('pwd'))
                        // Initialize Terraform
                        // Apply or Destroy Infrastructure based on parameter
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
                    // Extract IPs from Terraform output and update Ansible inventory
                    def ips = sh(script: "terraform output -json instance_public_ips | jq -r '.[]'", returnStdout: true).trim()
                    writeFile(file: env.ANSIBLE_HOSTS_FILE, text: """
                        ---
                        all:
                          hosts:
                        ${ips.split('\n').collect { "    ${it}:" }.join('\n')}
                        """)
                }
            }
        }
        stage('Run Ansible Playbooks') {
            when {
                expression { params.ACTION == 'apply' }
            }
            steps {
                dir ('Ansible') {
                    sh('ansible-playbook -i ${ANSIBLE_HOSTS_FILE} main_playbook.yml')
                }
            }
        }
    }
}
