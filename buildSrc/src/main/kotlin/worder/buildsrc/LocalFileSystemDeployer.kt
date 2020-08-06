package worder.buildsrc

import java.io.File
import java.nio.file.Path

class LocalFileSystemDeployer(rootPath: Path) : ApplicationDeployer {
    private val rootFile: File = rootPath.toFile().also {
        it.mkdirs()
    }


    override fun uploadFile(path: String, file: File) {
        file.copyTo(rootFile.resolve(path))
    }

    override fun uploadFile(path: String, byteArray: ByteArray) {
        rootFile.resolve(path.substringBeforeLast("/", "")).mkdirs()
        rootFile.resolve(path).writeBytes(byteArray)
    }
}
