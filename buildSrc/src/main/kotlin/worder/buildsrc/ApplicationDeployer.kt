package worder.buildsrc

import java.io.File

interface ApplicationDeployer {
    fun uploadFile(path: String, file: File)
    fun uploadFile(path: String, byteArray: ByteArray)
}
