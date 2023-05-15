package md2html

object Md2HtmlExecute {

    @JvmStatic
    fun main(args: Array<String>) {

        val converter = MarkdownConverter(ConverterConfig())
        converter.convert()
    }
}