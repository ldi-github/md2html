package md2html

class FileItem(
    line: String
) {
    val lastModifiedLong: String
    val filePath: String

    init {

        val dateLength = "2023-05-16 02:37:06 +0900".length
        if (line.length < dateLength) {
            println()
        }
        filePath = line.substring(0, line.length - dateLength - 1)
        lastModifiedLong = line.substring(line.length - dateLength)
    }

    val lastModified: String
        get() {
            return lastModifiedLong.substring(0, 10)
        }

    override fun toString(): String {
        return "$lastModifiedLong $filePath"
    }
}