package md2html

import org.json.JSONObject
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class ConverterConfig(
    val configFile: String = Const.CONVERTER_CONFIG_FILE,
    val args: List<String> = listOf()
) {
    lateinit var jso: JSONObject
    lateinit var inputDirectory: Path
    lateinit var outputDirectory: Path
    var generateSitemap = true
    lateinit var siteRoot: String

    val includeGtag: Boolean

    val templatePath: Path
        get() {
            return inputDirectory.resolve("_template.html")
        }

    init {
        includeGtag = args.contains("gtag=true")
        loadFromFile()
    }

    private fun getJSONObjectFromFile(file: String): JSONObject {

        val data = File(file).readText()
        return JSONObject(data)
    }

    private fun getString(key: String): String? {

        return if (jso.has(key)) jso.getString(key) else null
    }

    /**
     * loadFromFile
     */
    fun loadFromFile() {

        jso = getJSONObjectFromFile(file = configFile)

        inputDirectory = Path.of(getString("inputDirectory") ?: Const.INPUT_DIRECTORY)
        outputDirectory = Path.of(getString("outputDirectory") ?: Const.OUTPUT_DIRECTORY)
        generateSitemap = (getString("generateSitemap") ?: Const.GENERATE_SITEMAP) == "true"
        siteRoot = getString("siteRoot") ?: throw IllegalArgumentException("siteRoot is required.")
        if (siteRoot.endsWith("/").not()) {
            siteRoot = "$siteRoot/"
        }
    }

    /**
     * getHeadTemplate
     */
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
    ${'$'}{gtag}
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

    var _lastGtag: String? = null

    /**
     * getGtag
     */
    fun getGtag(): String {

        if (includeGtag.not()) {
            return ""
        }

        val templatePath = inputDirectory.resolve("_template_gtag")
        if (Files.exists(templatePath).not()) {
            return ""
        }

        _lastGtag = _lastGtag ?: templatePath.toFile().readText()
        return _lastGtag!!
    }
}