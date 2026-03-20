package ua.kpi.ipze.part.services.httpClient

import io.ktor.client.HttpClient

object HttpClientProvider {
    val client = HttpClient()
}