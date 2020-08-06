package worder.buildsrc

import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import java.io.File
import java.util.zip.Adler32

class ApplicationDescriptor(
        @Json(index = 2) val mainClass: String,
        @Json(index = 3) val envArguments: List<String>,
        @Json(index = 4) val usedModules: String,
        modulePath: List<Library>,
        classPath: List<Library>
) {
    companion object {
        fun fromJson(jsonString: String): ApplicationDescriptor = Klaxon().parse(jsonString)!!
    }


    @Json(index = 5)
    val modulePath: List<Library> = modulePath.onEach {
        if (!it.path.startsWith("modulePath"))
            it.path = "modulePath/${it.path}"
    }

    @Json(index = 6)
    val classPath: List<Library> = classPath.filter { !modulePath.contains(it) }.onEach {
        if (!it.path.startsWith("classPath"))
            it.path = "classPath/${it.path}"
    }

    @Json(index = 1)
    val OS: String = System.getProperty("os.name")


    fun toJson(): String = Klaxon().toJsonString(this)


    class Library(
            @Json(index = 1) var path: String,
            @Json(index = 2) val checksum: Long,
            @Json(index = 3) val size: Long
    ) {
        constructor(file: File) : this(
                path = file.name,
                checksum = file.checksum(),
                size = file.length()
        ) {
            this.file = file
        }


        @Json(ignored = true)
        var file: File? = null


        override fun hashCode(): Int {
            var result = path.hashCode()
            result = 31 * result + size.hashCode()
            result = 31 * result + checksum.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Library) return false

            if (path != other.path) return false
            if (size != other.size) return false
            if (checksum != other.checksum) return false

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
