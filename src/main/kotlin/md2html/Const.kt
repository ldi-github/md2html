package md2html

object Const {

    const val CONVERTER_CONFIG_FILE = "md2htmlConfig.json"
    const val INPUT_DIRECTORY = "doc/markdown"
    const val OUTPUT_DIRECTORY = "doc/out"
    const val GENERATE_SITEMAP = "true"
    const val BUILD_TEMP_MD2HTML = "build/tmp/md2html"
    const val FORBIDDEN_CHARACTERS_IN_FILE_NAME = ":*?\"<>|"

    const val GET_LAST_MODIFIED_LIST = "git ls-files -z | xargs -0 -n1 -I{} -- git log -1 --format=\"%ai {}\" {}"
}