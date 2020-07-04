package worder

import java.io.File
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class StampedSourceFile private constructor(
        private val sourceFile: File,
        private val fileContent: String,
        private val presentStamp: String?
) {
    companion object {
        private const val pathToStampPattern = "/sourceFileStampPattern.txt"
        private val stampPattern: String = String(this::class.java.getResourceAsStream(pathToStampPattern).readBytes())
        private val regexProperty = "(?<=<).*?(?=>)".toRegex()
        private val regexStampPattern: Regex = "^$stampPattern"
                .replace("*", "\\*")
                .replace("<[^<]*?_TIME>".toRegex(), "<\\\\d{2}/\\\\d{2}/\\\\d{4}, \\\\d{2}:\\\\d{2}:\\\\d{2} (AM|PM)>")
                .replace("<[A-Z_]*?>".toRegex(), "<.*?>")
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
            patternProperties.forEachIndexed { i, value ->
                check(value == supportedProperties[i]) {
                    "StampProperty declaration order and the order of property appearance in the pattern should be same"
                }
            }
        }


        fun fromFile(sourceFile: File): StampedSourceFile? {
            val fileContent = sourceFile.readText()
            val presentStamp = parseRawStamp(fileContent)
            val isStampValid = presentStamp?.let { isStampValid(it) }
            return if (isStampValid == false) null else StampedSourceFile(sourceFile, fileContent, presentStamp)
        }


        private fun parseRawStamp(fileContent: String): String? =
                if (fileContent.startsWith("/**")) fileContent.substringBefore("*/") + "*/\n" else null

        private fun isStampValid(rawStamp: String): Boolean = rawStamp.matches(regexStampPattern)

        private fun LocalDateTime.toStampDateTime(): String = format(DateTimeFormatter.ofPattern("dd/MM/yyyy, hh:mm:ss a"))
    }


    private val properties: MutableMap<StampProperty, String?> = EnumMap<StampProperty, String?>(StampProperty::class.java)
    private var isConsumed = false


    init {
        if (presentStamp != null) {
            val values = regexProperty.findAll(presentStamp).map { it.value }.iterator()
            val titles = StampProperty.values().iterator()

            while (values.hasNext() && titles.hasNext()) {
                properties[titles.next()] = values.next()
            }

            check(properties.size == StampProperty.values().size) {
                "Error during parsing stamp's properties of $sourceFile!"
            }
        }
    }


    fun update() {
        check(!isConsumed) {
            "Stamp has already been updated! This object can't be reused. "
        }

        // there are only two possible states for this object (enforced by static factory)
        // 1) when a file doesn't have stamp and 2) when it has stamp
        // this object can't be created for invalid stamp or leading comment

        val lastFileModificationTime = LocalDateTime
                .ofEpochSecond(sourceFile.lastModified() / 1000, 0, ZoneOffset.ofHours(3))
                .toStampDateTime()

        val isItNewStamp = presentStamp == null
        val wasFileModified = lastFileModificationTime != properties[StampProperty.FILE_MODIFICATION_TIME]

        if (isItNewStamp || wasFileModified) {
            val who = "${javaClass.simpleName}.kt"
            val now = LocalDateTime.now().toStampDateTime()

            if (isItNewStamp) {
                properties[StampProperty.FILE_CREATION_TIME] = now
                properties[StampProperty.FILE_MODIFICATION_TIME] = now
                properties[StampProperty.STAMP_LAST_MODIFIED_BY] = who
                properties[StampProperty.STAMP_GENERATED_BY] = who
                properties[StampProperty.FILE_VERSION_NUMBER] = "1"
            } else {
                properties[StampProperty.FILE_MODIFICATION_TIME] = now
                properties[StampProperty.STAMP_LAST_MODIFIED_BY] = who
                properties[StampProperty.FILE_VERSION_NUMBER] = (properties[StampProperty.FILE_VERSION_NUMBER]!!.toInt() + 1).toString()
            }

            val newStamp = regexProperty.replace(stampPattern) { properties[StampProperty.valueOf(it.value)]!! }
            val newFileContent = if (presentStamp != null) fileContent.replace(presentStamp, newStamp) else "$newStamp\n$fileContent"
            sourceFile.writeText(newFileContent)
        }

        isConsumed = true
    }


    enum class StampProperty {
        STAMP_GENERATED_BY, STAMP_LAST_MODIFIED_BY,
        FILE_CREATION_TIME, FILE_MODIFICATION_TIME, FILE_VERSION_NUMBER
    }
}
