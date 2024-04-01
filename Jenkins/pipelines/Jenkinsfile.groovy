pipeline {
    agent any
    parameters {
        choice(name: 'ACTION', choices: ['apply', 'destroy'], description: 'Select Terraform action')
    }
    environment {
        ANSIBLE_HOSTS_FILE = 'inventory/hosts.yml'
    }
    stages {
        stage('Checkout') {
            steps {
                git(
                    url: 'https://github.com/dariusbrinzan/License_Config.git',
                    branch: 'master',
                    credentialsId: 'd434558d-fa83-4c36-94c3-e5496619e013'
                )
            }
        }
        stage('Terraform Init & Apply/Destroy') {
            steps {
                script {
                    // Initialize Terraform
                    sh('terraform init')
                    // Apply or Destroy Infrastructure based on parameter
                    if (params.ACTION == 'apply') {
                        sh('terraform apply -auto-approve')
                    } else {
                        sh('terraform destroy -auto-approve')
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
                    def ips = sh(script: "terraform output -json instance_ips | jq -r '.[]'", returnStdout: true).trim()
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
                // Run your Ansible playbooks
                sh('ansible-playbook -i ${ANSIBLE_HOSTS_FILE} your-playbook.yml')
            }
        }
    }
}
