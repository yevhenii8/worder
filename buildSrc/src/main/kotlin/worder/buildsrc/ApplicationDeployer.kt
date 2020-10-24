package worder.buildsrc

interface ApplicationDeployer {
    fun listCatalog(path: String = ""): List<String>
    fun downloadFile(path: String): ByteArray?
    fun uploadFile(path: String, byteArray: ByteArray)
    fun removeFile(path: String)
}
