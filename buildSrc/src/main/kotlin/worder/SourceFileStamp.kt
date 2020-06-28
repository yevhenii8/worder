package worder

import java.io.File
import java.net.URI

class SourceFileStamp private constructor(private val sourceFile: File) {
    companion object {
        private val rawPatternPath: URI = this::class.java.getResource("/sourceFileStampPattern.txt").toURI()
        private val rawPattern: String = File(rawPatternPath).readText()
        private val regexProperty = "(?<=<).*?(?=>)".toRegex()
        private val patternProperties: List<String> = regexProperty.findAll(rawPattern).map { it.value }.toList()
        private val regexPattern: Regex = rawPattern
                .replace("*", "\\*")
                .replace("<.*?>".toRegex(), "<.*?>")
                .toRegex()


        init {
            val unsupportedProperties = patternProperties - Property.values().map { it.name }
            check(unsupportedProperties.isEmpty()) {
                "SourceFileStamp doesn't support all the properties from [$rawPatternPath]: $unsupportedProperties"
            }
        }


        fun fromFile(sourceFile: File): SourceFileStamp? {
            val fileContent = sourceFile.readText()
            val rawStamp = parseRawStamp(fileContent)
            val isStampValid = rawStamp?.let { isStampValid(it) }
            return if (isStampValid == false) null else SourceFileStamp(sourceFile)
        }


        private fun parseRawStamp(fileContent: String): String? =
                if (fileContent.startsWith("/**")) fileContent.substringBefore("*/") + "*/\n" else null

        private fun isStampValid(rawStamp: String): Boolean = rawStamp.matches(regexPattern)
    }


    private val fileContent: String = sourceFile.readText()
    private val presentStamp: String? = parseRawStamp(fileContent)
    private val properties: MutableMap<String, String?> = patternProperties.map { it to null }.toMap(LinkedHashMap())


    init {
        if (presentStamp != null) {
            val values = regexProperty.findAll(presentStamp).iterator()
            val titles = properties.iterator()

            while (values.hasNext() && titles.hasNext()) {
                properties[titles.next().key] = values.next().value
            }
        }
    }


    fun update() {
        if (presentStamp == null) {
            // Set GENERATED_BY, GENERATION_TIME, CREATION_TIME
        }

        // Set CHECKED_BY, CHECK_TIME

        if (sourceFile.lastModified().toInt() != properties[Property.MODIFICATION_TIME.name]?.length) {
            // Set MODIFICATION_TIME, VERSION_NUMBER
        }
    }


    enum class Property {
        GENERATED_BY, GENERATION_TIME,
        CHECKED_BY, CHECK_TIME,
        CREATION_TIME, MODIFICATION_TIME, VERSION_NUMBER
    }
}
