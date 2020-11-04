package worder.buildsrc

import java.io.File
import java.io.IOException
import java.nio.file.Path

class FileSystemDeployer(rootPath: Path) : AppDeployer {
    private val rootAsFile: File = rootPath.toFile().also {
        it.mkdirs()
    }


    override fun listCatalog(path: String): List<String> {
        require(!path.startsWith("/")) {
            "You can't use absolute path here, passed value: $path"
        }

        return rootAsFile.resolve(path).list()!!.toList()
    }

    override fun downloadFile(path: String): ByteArray {
        require(!path.startsWith("/")) {
            "You can't use absolute path here, passed value: $path"
        }

        return rootAsFile.resolve(path).readBytes()
    }

    override fun uploadFile(path: String, byteArray: ByteArray, override: Boolean) {
        require(!path.startsWith("/")) {
            "You can't use absolute path here, passed value: $path"
        }

        val pathToUpload = rootAsFile.resolve(path)
        if (!override && pathToUpload.exists())
            throw IOException("File already exists!")

        rootAsFile.resolve(path.substringBeforeLast("/", "")).mkdirs()
        pathToUpload.writeBytes(byteArray)
    }

    override fun removeFile(path: String) {
        require(!path.startsWith("/")) {
            "You can't use absolute path here, passed value: $path"
        }

        rootAsFile.resolve(path).deleteRecursively()
    }

    override fun toString(): String = "${javaClass.simpleName}($rootAsFile)"
}
