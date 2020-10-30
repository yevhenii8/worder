package worder.buildsrc

import java.io.FileNotFoundException
import java.io.IOException
import java.net.Authenticator
import java.net.PasswordAuthentication
import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class BintrayDeployer(
        private val bintrayUser: String,
        private val bintrayKey: String,
        private val repository: String,
        private val `package`: String,
        private val version: String
) : AppDeployer {
    companion object {
        private val downstreamFilter = Regex("<pre>.*</pre>")
        private val downstreamPattern = Regex("<.*?>")
    }


    private val downstreamURL = "https://dl.bintray.com/$bintrayUser/$repository/"
    private val upstreamURL = "https://api.bintray.com/content/$bintrayUser/$repository/$`package`/$version/"
    private val httpClient = HttpClient.newBuilder()
            .authenticator(object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(bintrayUser, bintrayKey.toCharArray())
                }
            })
            .build()


    override fun listCatalog(path: String): List<String> {
        require(!path.startsWith("/")) {
            "You can't use absolute path here, passed value: $path"
        }

        return try {
            URL(downstreamURL + path)
                    .readText()
                    .lines()
                    .filter { downstreamFilter.matches(it) }
                    .map { downstreamPattern.replace(it, "") }
        } catch (e: FileNotFoundException) {
            emptyList()
        }
    }

    override fun downloadFile(path: String): ByteArray {
        require(!path.startsWith("/")) {
            "You can't use absolute path here, passed value: $path"
        }

        return URL(downstreamURL + path).readBytes()
    }

    override fun uploadFile(path: String, byteArray: ByteArray, override: Boolean) {
        require(!path.startsWith("/")) {
            "You can't use absolute path here, passed value: $path"
        }

        val request = HttpRequest.newBuilder(URI.create("$upstreamURL$path?publish=1&override=${if (override) 1 else 0}"))
                .PUT(HttpRequest.BodyPublishers.ofByteArray(byteArray))
                .build()
        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() != 201)
            throw IOException("BintrayAPI response code - ${response.statusCode()}: \n${response.body()}")
    }

    override fun removeFile(path: String) {
        require(!path.startsWith("/")) {
            "You can't use absolute path here, passed value: $path"
        }

        val request = HttpRequest.newBuilder(URI.create("https://api.bintray.com/content/$bintrayUser/$repository/$path"))
                .DELETE()
                .build()
        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() != 200)
            throw IOException("BintrayAPI response code - ${response.statusCode()}: \n${response.body()}")
    }
}
