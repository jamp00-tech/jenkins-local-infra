**Jenkins Local Infra**

This repository provisions a Docker-based Jenkins runtime used for CI/CD.

When the VM starts via Terraform + cloud-init:

- Docker is installed
- This repository is cloned into `/opt/app-runtime/jenkins`
- Docker Compose builds Jenkins
- Jenkins starts automatically
- Startup Groovy scripts create folders and pipeline jobs automatically

**Architecture**

```text
OCI VM
│
├── Docker daemon (host)
│
├── Jenkins container
│   ├── Maven
│   ├── Terraform
│   ├── OCI CLI
│   ├── Docker CLI
│   └── Pipeline jobs
│
└── OCIR
```

Jenkins uses the host Docker daemon through:

```yaml
/var/run/docker.sock:/var/run/docker.sock
```

This allows Jenkins to build, push, pull and run Docker images.

**Repositories**

Jenkins infrastructure repo:

`https://github.com/jamp00-tech/jenkins-local-infra.git`

Pipeline repo:

`https://github.com/jamp00-tech/jenkins-pipelines.git`

Application repos follow this pattern:

`https://github.com/jamp00-tech/<repo-name>.git`

Example:

`https://github.com/jamp00-tech/spring-boot-jwt-kafka.git`

**Automatically created Jenkins folders/jobs**

```text
pipelines/
├── build/
│   └── app-build
├── deploy/
│   └── app-deploy
scripts/
├── common/
├── docker/
└── git/
```

Created by:

`init/seed-job.groovy`

**Required Jenkins credentials**

Create in:

`Manage Jenkins → Credentials → Global`

Credential:

- ID: `ocir-credentials`
- Type: `Username with password`

Username format:

`<namespace>/<oci-user>`

Example:

`greg14puj5pf/javier.munoz.p@hotmail.com`

Get namespace:

```bash
oci os ns get
```

Password:

OCI Auth Token

Generate token:

`OCI Profile → Auth Tokens → Generate Token`

**Image versioning**

Version is read automatically from `pom.xml`:

```xml
<version>1.1.2</version>
```

Generated image example:

`sa-saopaulo-1.ocir.io/greg14puj5pf/spring-boot-jwt-kafka:1.1.2`

**Run Jenkins manually**

```bash
cd /opt/app-runtime/jenkins
docker compose up -d --build
```

**Validate tools inside Jenkins**

```bash
docker exec -it jenkins-jenkins-1 mvn -v
docker exec -it jenkins-jenkins-1 docker -v
docker exec -it jenkins-jenkins-1 terraform -v
docker exec -it jenkins-jenkins-1 oci --version
```

**Full flow**

```text
Terraform
→ OCI VM
→ cloud-init
→ Docker
→ Jenkins
→ Build pipeline
→ Docker image
→ OCIR
→ Deploy pipeline
```

