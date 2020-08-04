package worder.buildsrc

import java.io.File
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class StampedFile(
        private val sourceFile: File,
        private val useTransit: Boolean
) {
    @Suppress("DuplicatedCode")
    companion object {
        private const val pathToStampPattern = "/sourceFileStampPattern.txt"
        private val stampPattern: String = String(this::class.java.getResourceAsStream(pathToStampPattern).readBytes())
        private val regexStampPattern: Regex = "^$stampPattern"
                .replace("*", "\\*")
                .replace("<[^<]*?_TIME>".toRegex(), "<\\\\d{2}/\\\\d{2}/\\\\d{4}, \\\\d{2}:\\\\d{2}:\\\\d{2} (AM|PM)>")
                .replace("<[A-Z_]*?>".toRegex(), "<.*?>")
                .toRegex()

        private const val pathToStampPatternTransit = "/sourceFileStampPatternTransit.txt"
        private val stampPatternTransit: String = String(this::class.java.getResourceAsStream(pathToStampPatternTransit).readBytes())
        private val regexStampPatternTransit: Regex = "^$stampPatternTransit"
                .replace("*", "\\*")
                .replace("<[^<]*?_TIME>".toRegex(), "<\\\\d{2}/\\\\d{2}/\\\\d{4}, \\\\d{2}:\\\\d{2}:\\\\d{2} (AM|PM)>")
                .replace("<[A-Z_]*?>".toRegex(), "<.*?>")
                .toRegex()

        private val regexProperty = "(?<=<).*?(?=>)".toRegex()


        init {
            val patternProperties: List<String> = regexProperty.findAll(stampPattern).map { it.value }.toList()
            val supportedProperties: List<String> = StampProperty.values().map { it.name }

            // we are meant to support all the properties from the pattern
            val unsupportedProperties = patternProperties - supportedProperties
            check(unsupportedProperties.isEmpty()) {
                "SourceFileStamp doesn't support all the properties from [$pathToStampPattern]: $unsupportedProperties"
            }

            // StampProperty declaration order and the order of property appearance in the pattern should be same
            patternProperties.forEachIndexed { i, value ->
                check(value == supportedProperties[i]) {
                    "StampProperty declaration order and the order of property appearance in the pattern should be same"
                }
            }
        }
    }


    private val fileContent = sourceFile.readText()
    private val presentStamp = parseRawStamp(fileContent)
    private val properties = getProperties<StampProperty>()

    lateinit var validationResult: String
    val isStampValid = presentStamp?.let { if (useTransit) isStampValidTransit(it) else isStampValid(it) } ?: true


    fun updateStamp() {
        if (!isStampValid)
            error("Attempting to update invalid stamp!")

        if (useTransit) {
            val propertiesTransit = getProperties<StampPropertyTransit>()

            propertiesTransit.forEach { (k, v) ->
                properties[StampProperty.valueOf(k.toString())] = v
            }

            transitAction(properties)
        }

        val lastFileModificationTime = LocalDateTime
                .ofEpochSecond(sourceFile.lastModified() / 1000, 0, ZoneOffset.ofHours(3))
                .toStampDateTime()
        val isItNewStamp = presentStamp == null
        val wasFileModified = lastFileModificationTime != properties[StampProperty.FILE_MODIFICATION_TIME]

        if (isItNewStamp || wasFileModified || useTransit) {
            val who = "${javaClass.simpleName}.kt"
            val now = LocalDateTime.now().toStampDateTime()

            if (isItNewStamp) {
                properties[StampProperty.FILE_CREATION_TIME] = now
                properties[StampProperty.FILE_MODIFICATION_TIME] = now
                properties[StampProperty.STAMP_LAST_MODIFIED_BY] = who
                properties[StampProperty.STAMP_GENERATED_BY] = who
                properties[StampProperty.FILE_VERSION_NUMBER] = "1"
                properties[StampProperty.FILE_NAME] = sourceFile.name
            } else {
                properties[StampProperty.FILE_MODIFICATION_TIME] = now
                properties[StampProperty.STAMP_LAST_MODIFIED_BY] = who
                properties[StampProperty.FILE_VERSION_NUMBER] = (properties[StampProperty.FILE_VERSION_NUMBER]!!.toInt() + 1).toString()
            }

            val newStamp = regexProperty.replace(stampPattern) { properties[StampProperty.valueOf(it.value)]!! }
            val newFileContent = if (presentStamp != null) fileContent.replace(presentStamp, newStamp) else "$newStamp\n$fileContent"
            sourceFile.writeText(newFileContent)
        }
    }


    private inline fun <reified T : Enum<T>> getProperties(): MutableMap<T, String?> {
        val res = EnumMap<T, String?>(T::class.java)

        if (presentStamp != null) {
            val enumValues = enumValues<T>()
            val values = regexProperty.findAll(presentStamp).map { it.value }.iterator()
            val titles = enumValues.iterator()

            while (values.hasNext() && titles.hasNext()) {
                res[titles.next()] = values.next()
            }

            check(res.size == enumValues.size || useTransit) {
                "Count of properties in the stamp pattern and in the file is different!\n" +
                        "Processed file: $sourceFile\n"
            }
        }

        return res
    }

    private fun transitAction(properties: MutableMap<StampProperty, String?>) {
        properties[StampProperty.FILE_NAME] = sourceFile.name
    }

    private fun parseRawStamp(fileContent: String): String? =
            if (fileContent.startsWith("/**")) fileContent.substringBefore("*/") + "*/\n" else null

    private fun isStampValid(rawStamp: String): Boolean = when {
        !fileContent.endsWith("\n") -> {
            validationResult = "File should end with a newline: $sourceFile"
            false
        }
        !rawStamp.matches(regexStampPattern) -> {
            validationResult = "Invalid stamp format occurred: $sourceFile"
            false
        }
        properties[StampProperty.FILE_NAME] != sourceFile.name -> {
            validationResult = "Stamp's FILE_NAME property doesn't correspond to the actual file name: $sourceFile"
            false
        }
        else -> true
    }

    private fun isStampValidTransit(rawStamp: String): Boolean = rawStamp.matches(regexStampPatternTransit)

    private fun LocalDateTime.toStampDateTime(): String = format(DateTimeFormatter.ofPattern("dd/MM/yyyy, hh:mm:ss a"))


    enum class StampProperty {
        STAMP_GENERATED_BY, STAMP_LAST_MODIFIED_BY, FILE_NAME,
        FILE_CREATION_TIME, FILE_MODIFICATION_TIME, FILE_VERSION_NUMBER
    }

    @Suppress("unused")
    enum class StampPropertyTransit {
        STAMP_GENERATED_BY, STAMP_LAST_MODIFIED_BY,
        FILE_CREATION_TIME, FILE_MODIFICATION_TIME, FILE_VERSION_NUMBER
    }
}
