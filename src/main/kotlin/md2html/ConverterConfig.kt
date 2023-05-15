package md2html

import org.json.JSONObject
import java.io.File

class ConverterConfig(
    val configFile: String = Const.CONVERTER_CONFIG_FILE
) {
    lateinit var jso: JSONObject
    var inputDirectory: String = ""
    var outputDirectory: String = ""

    init {
        loadFromFile()
    }

    private fun getJSONObjectFromFile(file: String): JSONObject {

        val data = File(file).readText()
        return JSONObject(data)
    }

    fun loadFromFile() {

        jso = getJSONObjectFromFile(file = configFile)

        inputDirectory = if (jso.has("inputDirectory")) jso.getString("inputDirectory") else ""
        outputDirectory = if (jso.has("outputDirectory")) jso.getString("outputDirectory") else ""
    }

}