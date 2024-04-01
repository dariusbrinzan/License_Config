FROM jenkins/jenkins:lts

USER root

RUN apt-get update && \
    apt-get install -y software-properties-common curl unzip ansible

RUN curl -fsSL https://apt.releases.hashicorp.com/gpg | apt-key add - && \
    echo "deb [arch=amd64] https://apt.releases.hashicorp.com $(lsb_release -cs) main" > /etc/apt/sources.list.d/hashicorp.list && \
    apt-get update && apt-get install -y terraform

USER jenkins
