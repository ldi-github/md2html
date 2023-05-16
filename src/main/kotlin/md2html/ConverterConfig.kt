package md2html

import org.json.JSONObject
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class ConverterConfig(
    val configFile: String = Const.CONVERTER_CONFIG_FILE
) {
    lateinit var jso: JSONObject
    lateinit var inputDirectory: Path
    lateinit var outputDirectory: Path

    val templatePath: Path
        get() {
            return inputDirectory.resolve("_template.html")
        }

    init {
        loadFromFile()
    }

    private fun getJSONObjectFromFile(file: String): JSONObject {

        val data = File(file).readText()
        return JSONObject(data)
    }

    private fun getString(key: String): String {

        return if (jso.has(key)) jso.getString(key) else ""
    }

    fun loadFromFile() {

        jso = getJSONObjectFromFile(file = configFile)

        inputDirectory = Path.of(getString("inputDirectory"))
        outputDirectory = Path.of(getString("outputDirectory"))
    }

    fun getHeadTemplate(): String {

        if (Files.exists(templatePath)) {
            return templatePath.toFile().readText()
        }

        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <link rel="stylesheet" href="${'$'}{assets}/main.css"/>
    <title>${'$'}{h1}</title>
</head>
<body>
<div id="readme" class="Box">
    <article class="markdown-body container-lg">
        ${'$'}{contentHtml}
    </article>
</div>
</body>
</html>
        """.trimIndent()
    }
}