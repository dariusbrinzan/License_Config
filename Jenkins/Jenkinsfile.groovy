pipeline {
    agent any
    parameters {
        string(name: 'INSTANCE_IP', defaultValue: '', description: 'IP address of the instance')
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
                    credentialsId: ''
                )
            }
        }
        stage('Update Ansible Inventory') {
            steps {
                script {
                    def ip = params.INSTANCE_IP.trim()
                    def inventory = """
                    all:
                      hosts:
                        instance:
                          ansible_host: ${ip}
                    """
                    print(inventory)
                    writeFile(file: env.ANSIBLE_HOSTS_FILE, text: inventory)
                    sh('cat ${ANSIBLE_HOSTS_FILE}')
                }
            }
        }
        stage('Run Ansible Playbook') {
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
