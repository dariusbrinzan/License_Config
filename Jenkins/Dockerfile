FROM jenkins/jenkins:lts

USER jenkins

RUN apt-get update && apt-get install -y ansible terraform

CMD ["/usr/sbin/jenkins"]