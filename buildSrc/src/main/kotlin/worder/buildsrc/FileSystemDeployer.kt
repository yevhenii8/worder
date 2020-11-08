package worder.buildsrc

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

class FileSystemDeployer(rootPath: Path) : AppDeployer {
    private val root: Path by lazy {
        Files.createDirectories(rootPath)
    }


    override fun listCatalog(path: String): List<String> {
        require(!path.startsWith("/")) {
            "You can't use absolute path here, passed value: $path"
        }

        return Files.list(root.resolve(path)).map { it.toString() }.toList()
    }

    override fun downloadFile(path: String): ByteArray {
        require(!path.startsWith("/")) {
            "You can't use absolute path here, passed value: $path"
        }

        return Files.readAllBytes(root.resolve(path))
    }

    override fun uploadFile(path: String, byteArray: ByteArray, override: Boolean) {
        require(!path.startsWith("/")) {
            "You can't use absolute path here, passed value: $path"
        }

        val pathToUpload = root.resolve(path)
        if (!override && Files.exists(pathToUpload))
            throw IOException("File already exists!")

        Files.createDirectories(root.resolve(path.substringBeforeLast("/", "")))
        Files.write(pathToUpload, byteArray)
    }

    override fun deleteFile(path: String) {
        require(!path.startsWith("/")) {
            "You can't use absolute path here, passed value: $path"
        }

        Files.delete(root.resolve(path))
    }

    override fun toString(): String = "${javaClass.simpleName}($root)"
}
