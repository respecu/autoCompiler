package ohrae.biz.model

import java.io.File

class Project {
    lateinit var name: String
    private lateinit var _path: String
    var path: String
        get() {
            return _path
        }
        set(value) {
            this._path = value
            this.targetDirectory = File(value)
        }
    lateinit var resultPath: String
    lateinit var defaultBranch: String
    lateinit var outputFolder: String
    lateinit var buildScript: String
    lateinit var targetDirectory: File
    var packages: ArrayList<PackageItem> = ArrayList()

    override fun toString(): String {
        var string = "PROJECT ${name}\npath : ${path}, result path: ${resultPath}"
        for (item in packages) {
            string += "\n${item}"
        }
        return string
    }

    fun add(item: PackageItem) {
        packages.add(item)
    }
}