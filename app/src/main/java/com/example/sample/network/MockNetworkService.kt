package com.example.sample.network

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.fullPath
import io.ktor.http.headersOf
import java.time.LocalDate

object MockNetworkService {

    fun mockEngineClient(context: Context): HttpClient {
        val engine = MockEngine { request ->

            val fileName = request.url.fullPath.replace("/", "-")
                .removePrefix("-") + "-" + request.method.value.uppercase()
            val fileFullPath = "request/json/$fileName.json"

            val acceptanceType = requireNotNull(request.headers[HttpHeaders.Accept])
            val responseContentTypeHeader = HttpHeaders.ContentType to listOf(acceptanceType)

            when(request.url.fullPath) {

                "/v1/appointments" -> {
                    val response = getResponseString(context, fileFullPath)
                    respond(
                        response.convertMockResponseISODate(),
                        headers = headersOf(responseContentTypeHeader),
                        status = HttpStatusCode.OK
                    )
                }

                else -> respond(
                    "No Endpoint Found",
                    HttpStatusCode.NotImplemented
                )
            }
        }
        return HttpClient(engine) {
            buildConfigurations()
        }
    }
}

internal fun getResponseString(context: Context, urlFullPath: String): String {
    return runCatching {
        context.readAssetJson(urlFullPath)
    }.onFailure {
        it.printStackTrace()
        throw RuntimeException("Exception occurred: ${it.message}")
    }.getOrThrow()
}


internal fun Context.readAssetJson(filename: String): String  {
    println("Context.readAssetJson filename = $filename")
    val inputStream = assets.open(filename)
    return inputStream.bufferedReader().use { it.readText() }
}

internal fun String.convertMockResponseISODate(): String {
    return this.replace("%ISO_DATE%", LocalDate.now().toString())
        .replace("%ISO_DATE+1%", LocalDate.now().plusDays(1).toString())
        .replace("%ISO_DATE-1%", LocalDate.now().minusDays(1).toString())
}