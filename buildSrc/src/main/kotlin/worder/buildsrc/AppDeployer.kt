package worder.buildsrc

interface AppDeployer {
    fun listCatalog(path: String = ""): List<String>
    fun downloadFile(path: String): ByteArray
    fun uploadFile(path: String, byteArray: ByteArray, override: Boolean = false)
    fun removeFile(path: String)
}
