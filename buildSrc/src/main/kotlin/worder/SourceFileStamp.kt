package worder

import java.io.File
import java.net.URI
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class SourceFileStamp private constructor(
        private val sourceFile: File,
        private val fileContent: String,
        private val presentStamp: String?
) {
    companion object {
        private val pathToStampPattern: URI = this::class.java.getResource("/sourceFileStampPattern.txt").toURI()
        private val stampPattern: String = File(pathToStampPattern).readText()
        private val regexProperty = "(?<=<).*?(?=>)".toRegex()
        private val regexStampPattern: Regex = "^$stampPattern"
                .replace("*", "\\*")
                .replace("<.*?>".toRegex(), "<.*?>")
                .toRegex()


        init {
            val patternProperties: List<String> = regexProperty.findAll(stampPattern).map { it.value }.toList()
            val supportedProperties: List<String> = StampProperty.values().map { it.name }

            // we are meant to support all the properties from the pattern
            val unsupportedProperties = patternProperties - supportedProperties
            check(unsupportedProperties.isEmpty()) {
                "SourceFileStamp doesn't support all the properties from [$pathToStampPattern]: $unsupportedProperties"
            }

            // StampProperty declaration order and the order of property appearance in the pattern should be same
            for (i in 0..patternProperties.size)
                if (patternProperties[i] != supportedProperties[i])
                    error("StampProperty declaration order and the order of property appearance in the pattern should be same")
        }


        fun fromFile(sourceFile: File): SourceFileStamp? {
            val fileContent = sourceFile.readText()
            val presentStamp = parseRawStamp(fileContent)
            val isStampValid = presentStamp?.let { isStampValid(it) }
            return if (isStampValid == false) null else SourceFileStamp(sourceFile, fileContent, presentStamp)
        }


        private fun parseRawStamp(fileContent: String): String? =
                if (fileContent.startsWith("/**")) fileContent.substringBefore("*/") + "*/\n" else null

        private fun isStampValid(rawStamp: String): Boolean = rawStamp.matches(regexStampPattern)
    }


    private val properties: MutableMap<StampProperty, String?> = EnumMap<StampProperty, String?>(StampProperty::class.java)
    private var isConsumed = false


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
        check(!isConsumed) {
            "Stamp has already been updated! This object can't be reused. "
        }

        // Updating Stamp's properties
        fun datetimeFormatter(datetime: LocalDateTime): String = datetime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
        val now = LocalDateTime.now()
        val who = "${javaClass.simpleName}.kt"

        properties[StampProperty.CHECKED_BY] = who
        properties[StampProperty.CHECK_TIME] = datetimeFormatter(now)

        if (presentStamp == null) {
            properties[StampProperty.GENERATED_BY] = who
            properties[StampProperty.GENERATION_TIME] = datetimeFormatter(now)
            properties[StampProperty.CREATION_TIME] = datetimeFormatter(obtainFileCreationTime() ?: now)
            properties[StampProperty.MODIFICATION_TIME] = datetimeFormatter(now)
            properties[StampProperty.VERSION_NUMBER] = "1"
        } else {
            val modificationTime = obtainFileModificationTime()
            if (modificationTime != null) {
                properties[StampProperty.MODIFICATION_TIME] = datetimeFormatter(modificationTime)
                properties[StampProperty.VERSION_NUMBER] = (properties[StampProperty.VERSION_NUMBER]!!.toInt() + 1).toString()
            }
        }

        // Creating a new Stamp and applying changes to the file
        val newStamp = regexProperty.replace(stampPattern) { properties[StampProperty.valueOf(it.value)]!! }
        val newFileContent = if (presentStamp != null) fileContent.replace(presentStamp, newStamp) else "$newStamp$fileContent"
        sourceFile.writeText(newFileContent)

        isConsumed = true
    }


    // returns file's creation date or null if it can't be determined
    private fun obtainFileCreationTime(): LocalDateTime? {
        TODO()
    }

    // returns file's last modification date or null if a file was not modified since the LAST STAMP CHANGE
    private fun obtainFileModificationTime(): LocalDateTime? {
        TODO()
    }


    enum class StampProperty {
        GENERATED_BY, GENERATION_TIME,
        CHECKED_BY, CHECK_TIME,
        CREATION_TIME, MODIFICATION_TIME, VERSION_NUMBER
    }
}
