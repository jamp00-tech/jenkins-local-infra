import jenkins.model.Jenkins
import com.cloudbees.hudson.plugins.folder.Folder
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition
import hudson.plugins.git.GitSCM
import hudson.plugins.git.BranchSpec
import hudson.plugins.git.UserRemoteConfig


def jenkins = Jenkins.get()

def crearCarpeta
crearCarpeta = { String ruta ->
    def partes = ruta.split("/")
    def parent = jenkins
    def fullPath = ""

    partes.each { parte ->
        fullPath = fullPath ? "${fullPath}/${parte}" : parte

        def existente = parent.getItem(parte)

        if (existente == null) {
            existente = parent.createProject(Folder.class, parte)
            println "✅ Carpeta creada: ${fullPath}"
        } else {
            println "ℹ️ Carpeta ya existe: ${fullPath}"
        }

        parent = existente
    }
}

def carpetas = [
    "pipelines/build",
    "pipelines/deploy",
    "pipelines/network",
    "scripts/common",
    "scripts/docker",
    "scripts/git"
]

carpetas.each { crearCarpeta(it) }


/************************************
 * BUILD JOB
 ************************************/

def buildFolder = jenkins.getItemByFullName("pipelines/build")

def buildJobName = "app-build"

def existingBuildJob = buildFolder.getItem(buildJobName)

if (existingBuildJob == null) {
    def job = buildFolder.createProject(WorkflowJob.class, buildJobName)

    def scm = new GitSCM(
        [new UserRemoteConfig(
            "https://github.com/jamp00-tech/jenkins-pipelines.git",
            null,
            null,
            null
        )],
        [new BranchSpec("*/main")],
        false,
        [],
        null,
        null,
        []
    )

    def definition = new CpsScmFlowDefinition(scm, "build/Jenkinsfile")
    definition.setLightweight(true)

    job.setDefinition(definition)
    job.save()

    println "✅ Job creado: pipelines/build/${buildJobName}"
} else {
    println "ℹ️ Job ya existe: pipelines/build/${buildJobName}"
}


/************************************
 * DEPLOY JOB
 ************************************/

def deployFolder = jenkins.getItemByFullName("pipelines/deploy")

def deployJobName = "app-deploy"

def existingDeployJob = deployFolder.getItem(deployJobName)

if (existingDeployJob == null) {
    def job = deployFolder.createProject(WorkflowJob.class, deployJobName)

    def scm = new GitSCM(
        [new UserRemoteConfig(
            "https://github.com/jamp00-tech/jenkins-pipelines.git",
            null,
            null,
            null
        )],
        [new BranchSpec("*/main")],
        false,
        [],
        null,
        null,
        []
    )

    def definition = new CpsScmFlowDefinition(scm, "deploy/Jenkinsfile")
    definition.setLightweight(true)

    job.setDefinition(definition)
    job.save()

    println "✅ Job creado: pipelines/deploy/${deployJobName}"
} else {
    println "ℹ️ Job ya existe: pipelines/deploy/${deployJobName}"
}


/************************************
 * NETWORK JOB
 ************************************/

def networkFolderPath = "pipelines/network"
crearCarpeta(networkFolderPath)

def networkFolder = jenkins.getItemByFullName(networkFolderPath)

def networkJobName = "network-deploy"

def existingNetworkJob = networkFolder.getItem(networkJobName)

if (existingNetworkJob == null) {
    def job = networkFolder.createProject(WorkflowJob.class, networkJobName)

    def scm = new GitSCM(
        [new UserRemoteConfig(
            "https://github.com/jamp00-tech/jenkins-pipelines.git",
            null,
            null,
            null
        )],
        [new BranchSpec("*/main")],
        false,
        [],
        null,
        null,
        []
    )

    def definition = new CpsScmFlowDefinition(scm, "network/Jenkinsfile")
    definition.setLightweight(true)

    job.setDefinition(definition)
    job.save()

    println "✅ Job creado: pipelines/network/${networkJobName}"
} else {
    println "ℹ️ Job ya existe: pipelines/network/${networkJobName}"
}


jenkins.save()

println "✅ Estructura de carpetas creada exitosamente"