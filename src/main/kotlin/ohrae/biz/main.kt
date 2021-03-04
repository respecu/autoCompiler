package ohrae.biz

import java.io.File
fun main(args: Array<String>) {
    var workingDirectory = File("")

    var saveFolder = workingDirectory.child("out/outputs")
    if (!saveFolder.exists()) {
        saveFolder.mkdirs()
    } else {
        saveFolder.deleteRecursively()
        saveFolder.mkdirs()
    }

    val file = File("src/main/resources/config/config_app.xml")
    val config = Config(file)
    val executor = Executor()

    println("file exists : ${file.exists()}")

    val gradlew = if(System.getProperty("os.name").startsWith("Windows")) {
        "./gradlew.bat"
    }
    else {
        "./gradlew"
    }

    for (i in 0 until config.size()) {
        for (project in config.projects) {
            println(project.toString())
            val projectFolder = saveFolder.child(project.outputFolder)
            projectFolder.mkdirs()

            for (item in project.packages) {
                executor.run(Command(project.targetDirectory, "git checkout ${item.branchName}"))
                executor.run(Command(project.targetDirectory, "$gradlew clean"))
                executor.run(Command(project.targetDirectory, "$gradlew ${project.buildScript}"))
                executor.run(Runnable {
                    /** save */
                    val directory = if(item.hasDirectory()) projectFolder.child(item.outputDirectory) else projectFolder
                    if (!directory.exists()) directory.mkdirs()
                    val output = project.targetDirectory.child(project.resultPath)
                    val target = directory.child(item.outputFileName)
                    output.copyTo(target, true)
                })
                break
            }
            executor.run(Command(project.targetDirectory, "git checkout ${project.defaultBranch}"))
            break
        }
    }
}

fun File.child(fileName: String): File {
    return if (fileName.startsWith("/")) File("${absolutePath}${fileName}")
    else File("${absolutePath}/${fileName}")
}
