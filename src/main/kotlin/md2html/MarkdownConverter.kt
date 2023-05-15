package md2html

import com.vladsch.flexmark.ext.autolink.AutolinkExtension
import com.vladsch.flexmark.ext.emoji.EmojiExtension
import com.vladsch.flexmark.ext.emoji.EmojiImageType
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.ext.typographic.TypographicExtension
import com.vladsch.flexmark.ext.wikilink.WikiLinkExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.parser.ParserEmulationProfile
import com.vladsch.flexmark.util.data.MutableDataSet
import com.vladsch.flexmark.util.misc.Extension
import org.jsoup.Jsoup
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class MarkdownConverter(
    val converterConfig: ConverterConfig,
    val options: MutableDataSet = MutableDataSet()
) {
    val inputDirectory: String
        get() {
            return converterConfig.inputDirectory
        }
    val outputDirectory: String
        get() {
            return converterConfig.outputDirectory
        }

    init {

        options
//            .set(HtmlRenderer.ESCAPE_HTML, true)
            .set(EmojiExtension.USE_IMAGE_TYPE, EmojiImageType.UNICODE_FALLBACK_TO_IMAGE)
            .set(TablesExtension.COLUMN_SPANS, false)
            .set(TablesExtension.APPEND_MISSING_COLUMNS, true)
            .set(TablesExtension.DISCARD_EXTRA_COLUMNS, true)
            .set(TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, true)
            .set(
                Parser.EXTENSIONS,
                mutableListOf<Extension>(
                    TablesExtension.create(),
                    StrikethroughExtension.create(),
                    AutolinkExtension.create(),
                    WikiLinkExtension.create(),
                    TypographicExtension.create()
                )
            )
            .set(
                AutolinkExtension.IGNORE_LINKS,
                "[^@:]+@[^@]+"
            )
            .setFrom(ParserEmulationProfile.GITHUB_DOC)
    }

    /**
     * convert
     */
    fun convert() {

        val parser = Parser.builder(options).build()
        val htmlRenderer = HtmlRenderer.builder(options).build()

        if (Files.exists(Path.of(outputDirectory)).not()) {
            File(outputDirectory).mkdirs()
        }

        copyFile("main.css")

        val mdFiles = File(inputDirectory).walkTopDown().filter { it.name.endsWith(".md") }
        for (mdFile in mdFiles) {
            convertCore(mdFile, parser, htmlRenderer)
        }
    }

    private fun copyFile(file: String) {

        val fromFile = Path.of(inputDirectory).resolve(file)
        if (Files.exists(fromFile).not()) {
            return
        }

        val toFile = Path.of(outputDirectory).resolve(file)
        Files.copy(fromFile, toFile, StandardCopyOption.REPLACE_EXISTING)
    }

    private fun getRelative(outputHtmlPath: Path): String {

        val outputFileDepth = outputHtmlPath.toString().split("/").count()
        val outputDirectoryDepth = outputDirectory.split("/").count()
        val diff = outputFileDepth - outputDirectoryDepth
        var s = ""
        for (i in 1..diff - 1) {
            s += "../"
        }
        return s
    }

    private fun convertCore(
        mdFile: File,
        parser: Parser,
        htmlRenderer: HtmlRenderer
    ) {
        println(mdFile.name)

        val outputFileName = mdFile.toString().removePrefix(inputDirectory).trimStart('/').trimStart('¥')
            .removeSuffix(".md") + ".html"
        val outputHtmlPath = Path.of(outputDirectory).resolve(outputFileName)

        val mdText = mdFile.readText()
        val mdDocument = parser.parse(mdText)
        val renderedHtml = htmlRenderer.render(mdDocument)

        val mainCss = getRelative(outputHtmlPath) + "main.css"

        val completeHtml = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <link rel="stylesheet" href="$mainCss" />
</head>
<body>
<div id="readme" class="Box">
<article class="markdown-body container-lg">
$renderedHtml
</article>
</div>
</body>
</html>
        """.trimIndent()

        val htmlDocument = Jsoup.parse(completeHtml)

        val aTags = htmlDocument.select("a")
        for (aTag in aTags) {
            val href = aTag.attr("href")
            if (href.endsWith(".md")) {
                aTag.attr("href", href.removeSuffix(".md") + ".html")
            }
        }

        val dir = outputHtmlPath.parent
//        val htmlFilePath = dir.resolve("${mdFile.nameWithoutExtension}.html")

        if (Files.exists(dir).not()) {
            dir.toFile().mkdirs()
        }

        val imgTags = htmlDocument.select("img")
        for (imgTag in imgTags) {
            val src = imgTag.attr("src")
            if (src.isNotBlank()) {
                val orgPath = Path.of(getSimplePath(basePath = mdFile.toString(), relativePath = src))
                val copiedPath = Path.of(getSimplePath(basePath = outputHtmlPath.toString(), relativePath = src))
                if (Files.exists(copiedPath.parent).not()) {
                    copiedPath.parent.toFile().mkdirs()
                }
                try {
                    Files.copy(orgPath, copiedPath, StandardCopyOption.REPLACE_EXISTING)
                } catch (t: Throwable) {
                    println(t)
                }
            }
        }

        File(outputHtmlPath.toUri()).writeText(htmlDocument.toString())

    }

    fun getSimplePath(basePath: String, relativePath: String): String {

        var base = Path.of(basePath).parent
        var relative = relativePath

        while (true) {
            if (relative.startsWith("../")) {
                base = base.parent
                relative = relative.substring("../".length)
            } else if (relative.startsWith("./")) {
                relative = relative.substring("./".length)
            } else {
                return base.resolve(relative).toString()
            }
        }
    }

}