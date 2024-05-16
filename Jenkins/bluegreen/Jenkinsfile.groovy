pipeline {
    agent any
    parameters {
        string(name: 'INSTANCE_IP', defaultValue: '', description: 'IP address of the instance')
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
