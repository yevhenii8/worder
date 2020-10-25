package worder.buildsrc

import java.io.File
import java.nio.file.Path

class LocalFileSystemDeployer(rootPath: Path) : ApplicationDeployer {
    private val rootAsFile: File = rootPath.toFile().also {
        it.mkdirs()
    }


    override fun listCatalog(path: String): List<String>? {
        require(!path.startsWith("/")) {
            "You can't use absolute path here, passed value: $path"
        }

        return rootAsFile.resolve(path).list()?.toList()
    }

    override fun downloadFile(path: String): ByteArray? {
        require(!path.startsWith("/")) {
            "You can't use absolute path here, passed value: $path"
        }

        val requestedFile = rootAsFile.resolve(path)

        return if (requestedFile.isFile) rootAsFile.resolve(path).readBytes() else null
    }

    override fun uploadFile(path: String, byteArray: ByteArray, override: Boolean): Boolean {
        require(!path.startsWith("/")) {
            "You can't use absolute path here, passed value: $path"
        }

        val pathToUpload = rootAsFile.resolve(path)
        if (!override && pathToUpload.exists())
            return false

        rootAsFile.resolve(path.substringBeforeLast("/", "")).mkdirs()
        pathToUpload.writeBytes(byteArray)
        return true
    }

    override fun removeFile(path: String): Boolean {
        require(!path.startsWith("/")) {
            "You can't use absolute path here, passed value: $path"
        }

        return rootAsFile.resolve(path).deleteRecursively()
    }
}
