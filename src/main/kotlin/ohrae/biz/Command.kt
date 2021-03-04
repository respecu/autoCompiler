package ohrae.biz

import java.io.File
import java.io.IOException
import java.io.InputStream

class Command(val targetDirectory: File, val string: String) {
    val builder: ProcessBuilder
    var process: Process? = null

    init {
        val parts = string.split("\\s".toRegex())
        builder = ProcessBuilder(*parts.toTypedArray())
    }

    fun start(): InputStream? {
        try {
            val proc = builder.directory(targetDirectory)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()
            this.process = proc
            return proc.inputStream
        } catch (e: IOException) {
            println(e.message)
            if (e.message?.endsWith("Permission denied") == true) {
                val parts = "chmod 777 gradlew".split("\\s".toRegex())
                val builder = ProcessBuilder(*parts.toTypedArray())
                val proc = builder.directory(targetDirectory)
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(ProcessBuilder.Redirect.PIPE)
                    .start()
                this.process = proc
                return start()
            }
        }
        return null
    }
}