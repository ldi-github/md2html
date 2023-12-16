package md2html

object Executor {

    @JvmStatic
    fun main(args: Array<String>) {

        val converterConfig = ConverterConfig(args = args.toList())

        MarkdownConverter(converterConfig).convert()

        SitemapGenerator(converterConfig).generate()
    }
}