package md2html

object Executor {

    @JvmStatic
    fun main(args: Array<String>) {

        MarkdownConverter(ConverterConfig(args = args.toList())).convert()
    }
}