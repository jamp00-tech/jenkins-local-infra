import jenkins.model.Jenkins
import com.cloudbees.hudson.plugins.folder.Folder

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
    "scripts/common",
    "scripts/docker",
    "scripts/git"
]

carpetas.each { crearCarpeta(it) }

jenkins.save()

println "✅ Estructura de carpetas creada exitosamente"