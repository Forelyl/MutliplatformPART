package ua.kpi.ipze.part.services.paletteApi

import co.touchlab.kermit.Logger
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ua.kpi.ipze.part.services.httpClient.HttpClientProvider

@Serializable
data class PaletteResponse(val result: List<List<Int>>)

private val Tag = PaletteRequest::class.simpleName ?: ""

object PaletteRequest {
    // TODO when adding new http requests / json parsing - extract this object / parsing - into util
    private val parseRequest = Json { ignoreUnknownKeys = true }

    suspend fun fetchPalette(): List<List<Int>> =
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            val path = "http://colormind.io/api/"
            val json = """
                {
                  "model": "default"
                }
            """
            Logger.d(Tag) { "POST $path body: $json" }

            val responseText = HttpClientProvider.client.post(path) {
                contentType(ContentType.Application.Json)
                setBody(json)
            }.bodyAsText()


            val response =
                runCatching { parseRequest.decodeFromString<PaletteResponse>(responseText) }
            // TODO make safer without throw
            if (response.isFailure) Logger.d(Tag) { "Failed to make request: ${response.exceptionOrNull()?.message}" }
            return@withContext response.getOrThrow().result

        }
}
