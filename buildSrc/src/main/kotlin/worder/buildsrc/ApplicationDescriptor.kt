package worder.buildsrc

import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import java.io.File
import java.time.LocalDateTime
import java.util.zip.Adler32

class ApplicationDescriptor(
        @Json(index = 1) val OS: String = System.getProperty("os.name"),
        @Json(index = 2) val descriptorVersion: Int = 0,
        @Json(index = 3) val mainClass: String,
        @Json(index = 4) val envArguments: List<String>,
        @Json(index = 5) val usedModules: String,
        @Json(index = 6) val modulePath: List<Artifact>,
        @Json(index = 7) val classPath: List<Artifact>
) {
    companion object {
        fun fromJson(jsonString: String): ApplicationDescriptor = Klaxon().parse(jsonString)!!
        fun calculatedName(): String = "WorderAppDescriptor-${System.getProperty("os.name")}.json"
    }


    @Json(ignored = true)
    val allArtifacts = modulePath + classPath

    fun toJson(): String = Klaxon().toJsonString(this)

    override fun toString(): String = "WorderAppDescriptor-$OS.json"


    class Artifact(
            @Json(index = 1) var name: String,
            @Json(index = 2) val checksum: Long,
            @Json(index = 3) val size: Long
    ) {
        constructor(file: File) : this(
                name = "artifacts/${file.name}",
                checksum = file.checksum(),
                size = file.length()
        ) {
            this.file = file
        }


        @Json(ignored = true)
        var file: File? = null


        override fun hashCode(): Int {
            var result = name.substringAfterLast("/").hashCode()
            result = 31 * result + size.hashCode()
            result = 31 * result + checksum.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Artifact) return false

            if (size != other.size) return false
            if (checksum != other.checksum) return false
            if (name.substringAfterLast("/") != other.name.substringAfterLast("/")) return false

            return true
        }
    }
}

private fun File.checksum(): Long = inputStream().use { input ->
    val checksum = Adler32()
    val buf = ByteArray(16384)
    var read: Int
    while (input.read(buf).also { read = it } > -1) checksum.update(buf, 0, read)
    checksum.value
}
