package ohrae.biz

import ohrae.biz.model.PackageItem
import ohrae.biz.model.Project
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.File

class Config(val file: File) {
    var isAvailable: Boolean
    var projects: ArrayList<Project> = ArrayList()

    init {
        isAvailable = file.isFile && file.exists()
        getParser()?.let {
            parse(it)
        }
    }

    private fun getParser(): XmlPullParser? {
        if (!isAvailable) return null
        val stream = file.inputStream()
        val factory = XmlPullParserFactory.newInstance()
        var parser = factory.newPullParser()

        parser?.let {
            it.setInput(stream.reader())
            return parser
        }
        return null
    }

    private fun parse(parser: XmlPullParser) {
        var event = parser.eventType

        var project: Project? = null
        var packageItem: PackageItem? = null
        var name = ""
        while (event != XmlPullParser.END_DOCUMENT) {
            when (event) {
                XmlPullParser.START_TAG -> {
                    name = parser.name
                    when (name) {
                        "project" -> {
                            if (project == null) {
                                project = Project()
                            }
                        }
                        "item" -> {
                            if (packageItem == null) {
                                for (i in 0 until parser.attributeCount) {
                                    when (parser.getAttributeName(i)) {
                                        "name" -> packageItem = PackageItem(parser.getAttributeValue(i))
                                    }
                                }
                            }
                        }
                    }
                }
                XmlPullParser.TEXT -> {
                    when (name) {
                        "name" -> project?.name = parser.text
                        "path" -> project?.path = parser.text
                        "result-path" -> project?.resultPath = parser.text
                        "default-branch" -> project?.defaultBranch = parser.text
                        "output-folder" -> project?.outputFolder = parser.text
                        "build-script" -> project?.buildScript = parser.text

//                        "package-list" -> {
//                            if (packages == null) {
//                                packages = ArrayList()
//                            }
//                        }
                        "directory" -> packageItem?.outputDirectory = parser.text
                        "output" -> packageItem?.outputFileName = parser.text
                    }
                }
                XmlPullParser.END_TAG -> {
                    when (parser.name) {
                        "project" -> {
                            project?.let {
                                this.projects.add(it)
                            }
                            project = null
                        }
                        "item" -> {
                            packageItem?.let {
                                project?.add(it)
                            }
                            packageItem = null
                        }
                    }
                    name = ""
                }
            }
            event = parser.next()
        }
    }

    fun size(): Int {
        return projects.size
    }
}