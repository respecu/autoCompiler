package ohrae.biz.model

class PackageItem(val branchName: String) {
    lateinit var outputDirectory: String
    lateinit var outputFileName: String

    override fun toString(): String {
        var directory = if (hasDirectory()) "directory : ${outputDirectory}, " else ""
        return "[${branchName}]\n${directory}file: ${outputFileName}"
    }

    fun hasDirectory(): Boolean {
        return ::outputDirectory.isInitialized && outputDirectory.isNotEmpty()
    }
}