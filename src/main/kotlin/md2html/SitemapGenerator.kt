package md2html

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.writeText

class SitemapGenerator(
    val converterConfig: ConverterConfig,
) {

    /**
     * generate
     */
    fun generate() {

        if (converterConfig.generateSitemap.not()) {
            return
        }

        println("/// Generating sitemap.xml")

        val workDir = Path.of(Const.BUILD_TEMP_MD2HTML)
        if (Files.exists(workDir).not()) {
            workDir.toFile().mkdirs()
        }

        val lastModifiedList = getLastModifiedList(workDir)
        val mdList = lastModifiedList.filter { it.filePath.endsWith(".md") }

        val sitemapContent = getSiteMapContent(mdList)
        val sitemapFile = converterConfig.outputDirectory.resolve("sitemap.xml")
        sitemapFile.writeText(sitemapContent)
        println(sitemapFile)

        println()
    }

    private fun getLastModifiedList(workDir: Path): List<FileItem> {

        val file = workDir.resolve("lastmodified.sh")
        file.writeText("git ls-files ${converterConfig.inputDirectory} -z | xargs -0 -n1 -I{} -- git log -1 --format=\"{} %ai\" {}")

        val processBuilder = ProcessBuilder("sh", file.toString())
        val process = processBuilder.start()

        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val list = reader.readText().split("\n").filter { it.isNotBlank() }.map {
            val line = it.removePrefix(converterConfig.inputDirectory.toString()).removePrefix("/")
            FileItem(line)
        }

        val exitCode = process.waitFor()
        if (exitCode != 0) {
            println("exitCode=$exitCode")
        }
        return list
    }

    private fun String.escapeUrl(): String {

        return this.replace("&", "&amp;")
            .replace("'", "&apos;")
            .replace("\"", "&quot;")
            .replace(">", "&gt;")
            .replace("<", "&lt;")
    }

    private fun getSiteMapContent(list: List<FileItem>): String {

        val sb = StringBuilder()
        sb.appendLine("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
        sb.appendLine("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">")

        for (item in list) {
            val file = item.filePath.removeSuffix(".md") + ".html"
            val loc = "${converterConfig.siteRoot}${file}".escapeUrl()
            sb.appendLine("  <url>")
            sb.appendLine("    <loc>$loc</loc>")
            sb.appendLine("    <lastmod>${item.lastModified}</lastmod>")
            sb.appendLine("  </url>")
        }

        sb.appendLine("</urlset>")
        return sb.toString()
    }
}