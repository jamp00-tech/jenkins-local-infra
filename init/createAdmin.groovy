import jenkins.model.Jenkins
import hudson.security.*

def jenkins = Jenkins.get()

def usuario = "admin"
def password = "admin123"

def realm = new HudsonPrivateSecurityRealm(false)
realm.createAccount(usuario, password)

jenkins.setSecurityRealm(realm)

def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
strategy.setAllowAnonymousRead(false)

jenkins.setAuthorizationStrategy(strategy)
jenkins.save()

println "✅ Usuario admin creado: ${usuario}"
println "⚠️ El setup wizard queda deshabilitado"
