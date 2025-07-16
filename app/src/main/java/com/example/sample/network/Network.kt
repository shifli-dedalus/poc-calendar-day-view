package com.example.sample.network

import android.content.Context
import com.example.sample.BuildConfig
import com.example.sample.network.MockNetworkService.mockEngineClient
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.preparePost
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.fullPath
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import kotlinx.serialization.json.Json

object Network {

    fun getHttpClient(context: Context): HttpClient {
        return HttpClient(engineFactory = Android) { buildConfigurations() }
            .takeUnless { BuildConfig.DEBUG } ?: mockEngineClient(context)
    }

}

suspend inline fun <reified T> HttpClient.send(
    method: HttpMethod,
    path: String,
    requestBody: Any? = null,
    contentType: ContentType = ContentType.Application.Json,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    dataProcesser: (Result<T>) -> Unit
): HttpResponse? {
    val fullUrl = path /* eg: base + path */

    val requestBuilder: HttpRequestBuilder.() -> Unit = {
        url(fullUrl)
        contentType(contentType)
        requestBody?.let {
            setBody(requestBody)
        }
    }

    println("HttpClient send: fullUrl = $fullUrl, requestBody = $requestBody, method = $method")

    val response: HttpResponse? = withContext(dispatcher) {
        return@withContext when (method) {
            HttpMethod.Get -> {
                get(fullUrl, requestBuilder)
            }

            HttpMethod.Post -> {
                post(fullUrl, requestBuilder)
            }

            HttpMethod.Put -> {
                put(fullUrl, requestBuilder)
            }

            HttpMethod.Patch -> {
                preparePost {  }
                patch(fullUrl, requestBuilder)
            }

            HttpMethod.Delete -> {
                delete(fullUrl, requestBuilder)
            }

            else -> { null }
        }
    }

    println("HttpClient send: response = $response")
    handleHttpResponse(response, dataProcesser)

    return response
}

suspend inline fun <reified T> handleHttpResponse2(
    response: HttpResponse?,
    processor: (Result<T>) -> Unit,
) {
    response?.let {
        if (response.status.isSuccess()) {
            val data = response.body<T>()
            processor(
                Result.success(data)
            )
        } else {
            processor(
                Result.failure(
                    IOException("Unauthorized. ${response.status.description}")
                )
            )
        }
    }
}


suspend inline fun <reified T> handleHttpResponse(
    response: HttpResponse?,
    processor: (Result<T>) -> Unit,
) {
    response?.let {
        println("handleHttpResponse status = ${response.status.value}")
        when(response.status.value) {
            in 200..299 -> {
                //block.invoke(Result.success(response.body()))

                processor(
                    Result.success(response.body<T>())
                )
            }

            401 -> processor(
                Result.failure(
                IOException("Unauthorized. ${response.status.description}")
                )
            )

            404 -> processor(
                Result.failure(
                IOException("Not Found. ${response.status.description}")
                )
            )

            in 500..599 ->
                processor(
                    Result.failure(
                    IOException("Something went wrong. ${response.status.description}")
                    )
                )

            else -> {
                processor(
                    Result.failure(
                    IOException("Unknow Error. ${response.status.description}")
                    )
                )
            }
        }
    }
}


internal fun HttpClientConfig<*>.buildConfigurations() {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
    install(Logging) {
        level = LogLevel.ALL
        logger = Logger.ANDROID
    }
}

suspend inline fun <T> Result<T>.onSuccessSuspend(
    action: suspend (value: T) -> Unit
): Result<T> {

    if (isSuccess) action(getOrNull() as T)

    return this
}