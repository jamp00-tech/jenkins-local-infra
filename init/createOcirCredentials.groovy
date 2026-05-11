import jenkins.model.Jenkins
import com.cloudbees.plugins.credentials.CredentialsScope
import com.cloudbees.plugins.credentials.SystemCredentialsProvider
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl

def runCommand(String command) {
    def process = ["bash", "-c", command].execute()
    def output = new StringBuffer()
    def error = new StringBuffer()

    process.consumeProcessOutput(output, error)
    process.waitFor()

    if (process.exitValue() != 0) {
        println "❌ Command failed: ${command}"
        println "❌ Error: ${error.toString()}"
        return null
    }

    return output.toString().trim()
}

def readSecret(String secretId) {
    return runCommand("""
        oci secrets secret-bundle get \
          --auth instance_principal \
          --secret-id ${secretId} \
          --query 'data."secret-bundle-content".content' \
          --raw-output | base64 -d
    """)
}

def credentialsId = "ocir-credentials"
def description = "OCIR credentials loaded from OCI Vault"

def usernameSecretId = System.getenv("OCIR_USERNAME_SECRET_ID")
def tokenSecretId = System.getenv("OCIR_TOKEN_SECRET_ID")

if (!usernameSecretId || !tokenSecretId) {
    println "⚠️ OCIR secret IDs not found in environment variables. Skipping credential creation."
    return
}

def username = readSecret(usernameSecretId)
def token = readSecret(tokenSecretId)

if (!username || !token) {
    println "❌ Could not read OCIR secrets from OCI Vault. Skipping credential creation."
    return
}

def credentialsStore = SystemCredentialsProvider.getInstance().getStore()
def domain = com.cloudbees.plugins.credentials.domains.Domain.global()

def existingCredential = SystemCredentialsProvider.getInstance()
    .getCredentials()
    .find { it.id == credentialsId }

def newCredential = new UsernamePasswordCredentialsImpl(
    CredentialsScope.GLOBAL,
    credentialsId,
    description,
    username,
    token
)

if (existingCredential) {
    credentialsStore.updateCredentials(domain, existingCredential, newCredential)
    println "✅ Jenkins credential updated: ${credentialsId}"
} else {
    credentialsStore.addCredentials(domain, newCredential)
    println "✅ Jenkins credential created: ${credentialsId}"
}

Jenkins.get().save()
