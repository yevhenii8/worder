package worder.buildsrc

import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class ApplicationDescriptor(
        @Json(index = 3) val descriptorVersion: String,
        @Json(index = 4) val appVersion: String,
        @Json(index = 5) val mainClass: String,
        @Json(index = 6) val envArguments: List<String>,
        @Json(index = 7) val usedModules: String,
        @Json(index = 8) val modulePath: List<Artifact>,
        @Json(index = 9) val classPath: List<Artifact>
) {
    companion object {
        fun fromJson(jsonString: String): ApplicationDescriptor = Klaxon().parse(jsonString)!!
        fun currentName(): String = "WorderAppDescriptor-${System.getProperty("os.name")}.json"
    }


    @Json(index = 1)
    val name: String = "WorderAppDescriptor-${System.getProperty("os.name")}.json"

    @Json(index = 2)
    val generated: String = LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))

    @Json(ignored = true)
    val allArtifacts: List<Artifact> = modulePath + classPath


    fun toJson(): String = Klaxon().toJsonString(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ApplicationDescriptor

        if (descriptorVersion != other.descriptorVersion) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = descriptorVersion.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }


    class Artifact(@Json(ignored = true) val file: File?) {
        @Json(index = 1)
        val name: String = "artifacts/${file?.name}"

        @Json(index = 2)
        val size: Long = file?.length() ?: 0


        override fun hashCode(): Int {
            var result = name.substringAfterLast("/").hashCode()
            result = 31 * result + size.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Artifact) return false

            if (size != other.size) return false
            if (name != other.name) return false

            return true
        }
    }
}
