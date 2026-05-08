import jenkins.model.Jenkins
import hudson.security.*
import jenkins.security.*

def jenkins = Jenkins.getInstance()

def usuario = "admin"
def password = "admin123"

// Crear usuario admin
def user = jenkins.getSecurityRealm().createAccount(usuario, password, usuario + "@jenkins.local")
def strategy = new FullControlOnceLoggedInAuthorizationStrategy()

strategy.setAllowAnonymousRead(false)
jenkins.setAuthorizationStrategy(strategy)
jenkins.setSecurityRealm(jenkins.getSecurityRealm())

jenkins.save()

println "✅ Usuario admin creado: $usuario / $password"
println "⚠️ El setup wizard queda deshabilitado"
