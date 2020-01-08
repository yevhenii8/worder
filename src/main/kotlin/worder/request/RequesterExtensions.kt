package worder.request

import kotlinx.coroutines.future.await
import worder.Word
import worder.request.implementations.Cambridge
import worder.request.implementations.Lingvo
import worder.request.implementations.Macmillan
import worder.request.implementations.WooordHunt
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


val client: HttpClient = HttpClient.newHttpClient()

suspend fun Word.sendAsyncRequest(url: String): String {
    return if (!name.contains(" ")) {
        val request = HttpRequest.newBuilder(URI.create(url)).build()
        val response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).await()

        if (response.statusCode() != 200)
            throw IOException("HTTP request failed with code ${response.statusCode()}")

        response.body()!!
    } else
        ""
}


fun getDefaultRequesters(): Set<Requester> = setOf(
    Lingvo.newInstance(),
    Macmillan.newInstance(),
    WooordHunt.newInstance(),
    Cambridge.newInstance()
)
