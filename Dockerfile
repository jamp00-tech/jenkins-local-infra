FROM jenkins/jenkins:lts

USER root

# Dependencias base
RUN apt-get update && \
    apt-get install -y \
    wget \
    unzip \
    curl \
    maven \
    python3 \
    python3-venv \
    python3-full && \
    rm -rf /var/lib/apt/lists/*

# Terraform
RUN wget https://releases.hashicorp.com/terraform/1.7.0/terraform_1.7.0_linux_amd64.zip && \
    unzip terraform_1.7.0_linux_amd64.zip && \
    mv terraform /usr/local/bin/ && \
    rm terraform_1.7.0_linux_amd64.zip

# OCI CLI
RUN curl -L https://raw.githubusercontent.com/oracle/oci-cli/master/scripts/install/install.sh > install_oci.sh && \
    chmod +x install_oci.sh && \
    ./install_oci.sh \
      --install-dir /opt/oci-cli \
      --exec-dir /usr/local/bin \
      --accept-all-defaults && \
    rm install_oci.sh

# Directorio terraform
RUN mkdir -p /terraform && \
    chown -R jenkins:jenkins /terraform

# Plugins
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt

# Scripts init
COPY init/*.groovy /usr/share/jenkins/ref/init.groovy.d/

# Deshabilitar wizard inicial
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"

# Instalar plugins
RUN jenkins-plugin-cli \
    --plugin-file /usr/share/jenkins/ref/plugins.txt

USER jenkins
