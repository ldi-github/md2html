package md2html

object Md2HtmlExecute {

    @JvmStatic
    fun main(args: Array<String>) {

        val converterConfig = ConverterConfig()

        val converter = MarkdownConverter(converterConfig)
        converter.convert()

        val sitemapGenerator = SitemapGenerator(converterConfig)
        sitemapGenerator.generate()
    }
}