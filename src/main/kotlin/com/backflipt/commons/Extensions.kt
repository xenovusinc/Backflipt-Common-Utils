package com.backflipt.commons

import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.client.*
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * Extension Function for Map which returns the value for the given key as String
 * or null if the value for the given key is not present
 *
 * @receiver Map
 * @param key is the Key of the Map
 * @returns value for the given key as String or null if the value for the given key is not present
 */
fun Map<String, *>.getStringOrNull(key: String) = this[key] as? String

/**
 * Extension Function for Map which returns the value for the given key as String
 * or throws exception if the value for the given key is not present
 *
 * @receiver Map
 * @param key is the Key of the Map
 * @returns the value for the given key as String
 */
fun Map<String, Any?>.getStringOrException(key: String) = this.getStringOrNull(key)!!

/**
 * Extension Function for Map which returns the value for the given key  as String
 * or Default Value if the value for the given key is not present
 *
 * @receiver Map
 * @param key is the key of the Map
 * @param default is the default Map
 * @returns the value for the given key as String
 */
fun Map<String, *>.getStringOrDefault(
        key: String,
        default: String = ""
) = this.getStringOrNull(key) ?: default


/**
 * Extension Function for Map which returns the value for the given key as List
 * or null if the value for the given key  is not present
 *
 * @receiver Map
 * @param key is the Key of the Map
 * @returns the value for the given key as List
 */
fun <E> Map<String, *>.getListOrNull(key: String) = try {
    getListOrException<E>(key)
} catch (_: Exception) {
    null
}


/**
 * Extension Function for String which returns the Base64Decoded String
 *
 * @receiver String
 * @returns decoded string
 */
fun String.toBase64DecodedString(): String {
    return Base64Utils.decode(this).toString(Charset.defaultCharset())
}

/**
 * Creates Response Exception object while http request call
 *
 * @param response: ClientResponse
 * @param request: HttpRequest
 * @return WebClientResponseException
 */
private suspend fun createResponseException(
        response: ClientResponse,
        request: HttpRequest? = null
): WebClientResponseException {
    return DataBufferUtils
            .join(response.body(BodyExtractors.toDataBuffers()))
            .map { dataBuffer ->
                val bytes = ByteArray(dataBuffer.readableByteCount())
                dataBuffer.read(bytes)
                DataBufferUtils.release(dataBuffer)
                bytes
            }
            .defaultIfEmpty(ByteArray(0))
            .map { bodyBytes ->
                val charset = response.headers()
                        .contentType()
                        .map { it.charset }
                        .orElse(StandardCharsets.ISO_8859_1)!!
                if (HttpStatus.resolve(response.rawStatusCode()) != null)
                    WebClientResponseException.create(response.statusCode().value(), response.statusCode().reasonPhrase, response.headers().asHttpHeaders(), bodyBytes, charset, request)
                else UnknownHttpStatusCodeException(response.rawStatusCode(), response.headers().asHttpHeaders(), bodyBytes, charset, request)
            }
            .awaitFirst()
}


/**
 * Extension function for WebClient.RequestHeadersSpec which consumes if there is any error while making http call
 *
 * @receiver WebClient.RequestHeadersSpec
 * @param onError: callback function which will be called if there is any error during http call
 * @return ClientResponse
 */
suspend fun WebClient.RequestHeadersSpec<out WebClient.RequestHeadersSpec<*>>.awaitExchangeAndThrowError(
        onError: ((WebClientResponseException) -> Unit)? = null
): ClientResponse {
    return awaitExchange()
            .also { response ->
                if (response.statusCode().isError) {
                    val excp = createResponseException(response)
                    if (onError != null) onError(excp)
                    throw excp
                }
            }
}


/**
 * This function will calculate the execution time of the Passed Function
 *
 * @param function Function
 * @returns the Pair of Function Result and the Execution Time
 */
suspend fun <T> measureTimeMillisPair(function: suspend () -> T): Pair<T, Long> {
    val startTime = System.currentTimeMillis()
    val result: T = function()
    val endTime = System.currentTimeMillis()

    return Pair(result, endTime - startTime)
}

/**
 * Extension Function for Map which returns the value for the given key as List
 * or throws Exception if the value for the given key is not present
 *
 * @receiver Map
 * @param key is the Key of the Map
 * @returns value for the given key as List
 */
fun <E> Map<String, *>.getListOrException(key: String) = JsonParser.convertValue<List<E>>(this[key]!!)

/**
 * Extension Function for Map which returns the value for the given key as List
 * or Default List if the value for the given key is not present
 *
 * @receiver Map
 * @param key is the Key of the Map
 * @param default is the Default List
 * @returns the value for the given key as List
 */
fun <E> Map<String, *>.getListOrDefault(
        key: String,
        default: List<E> = emptyList()
): List<E> = this.getListOrNull(key) ?: default


/**
 * Extension Function for Map which returns the value for the given key as Map
 * or throws Exception if the value for the given key is not present
 *
 * @receiver Map
 * @param key is the Key of the Map
 * @returns the value for the given key as Map
 */
fun <K, V> Map<String, *>.getMapOrException(key: String): Map<K, V> {
    return JsonParser.convertValue<Map<K, V>>(this[key]!!)
}

/**
 * Extension Function for Map which returns the value for the given key as Map
 * or null if the value for the given key is not present
 *
 * @receiver Map
 * @param key is the Key of the Map
 * @returns value for the given key as Map
 */
fun <K, V> Map<String, *>.getMapOrNull(key: String) =
        try {
            getMapOrException<K, V>(key)
        } catch (e: Exception) {
            null
        }

/**
 * Extension Function for Map which returns the value for the given key as Map
 * or Default Map if the value for the given key is not present
 *
 * @receiver Map
 * @param key is the Key of the Map
 * @param defaultValue is the Default Map
 * @returns the value for the given key as Map
 */
fun <K, V> Map<String, *>.getMapOrDefault(
        key: String,
        defaultValue: Map<K, V> = emptyMap()
): Map<K, V> {
    return getMapOrNull(key) ?: defaultValue
}


fun Any.serialize() = JsonParser.serialize(this)

/**
 * Extension Function for String which returns the Map
 *
 * @receiver String
 * @returns Map
 */
fun String.deserialize() = JsonParser.deserialize(this)

inline fun <reified T : Any> Any.convertValue() = JsonParser.convertValue<T>(this)

/**
 * Extension Function for String which returns the urlEncoded String
 *
 * @receiver String
 * @returns encoded string
 */
fun String.urlEncode(): String = URLEncoder.encode(this, Charsets.UTF_8.toString())

fun String.urlDecode(): String = URLDecoder.decode(this, Charsets.UTF_8.toString())

/**
 * Creates Response Exception object while making http request call
 *
 * @param response: ClientResponse
 * @param request: HttpRequest
 * @return WebClientResponseException
 */
private suspend fun createSalesForceResponseException(
        response: ClientResponse,
        request: HttpRequest? = null
): WebClientResponseException {
    return DataBufferUtils
            .join(response.body(BodyExtractors.toDataBuffers()))
            .map { dataBuffer ->
                val bytes = ByteArray(dataBuffer.readableByteCount())
                dataBuffer.read(bytes)
                DataBufferUtils.release(dataBuffer)
                bytes
            }
            .defaultIfEmpty(ByteArray(0))
            .map { bodyBytes ->
                val charset = response.headers()
                        .contentType()
                        .map { it.charset }
                        .orElse(StandardCharsets.ISO_8859_1)!!
                val bodyString = bodyBytes.toString(Charset.defaultCharset())
                val statusCode = if (response.rawStatusCode() == 403 && ("missing_oauth_token" == bodyString.toLowerCase() || "bad_oauth_token" in bodyString.toLowerCase())) HttpStatus.UNAUTHORIZED
                else response.statusCode()
                if (HttpStatus.resolve(response.rawStatusCode()) != null)
                    WebClientResponseException.create(
                            statusCode.value(),
                            statusCode.reasonPhrase,
                            response.headers().asHttpHeaders(),
                            bodyBytes,
                            charset,
                            request
                    )
                else UnknownHttpStatusCodeException(response.rawStatusCode(), response.headers().asHttpHeaders(), bodyBytes, charset, request)
            }
            .awaitFirst()
}

/**
 * Extension function for WebClient.RequestHeadersSpec which consumes if there is any error while making http call
 *
 * @receiver WebClient.RequestHeadersSpec
 * @param onError: callback function which will be called if there is any error during http call
 * @return ClientResponse
 */
suspend fun WebClient.RequestHeadersSpec<out WebClient.RequestHeadersSpec<*>>.awaitExchangeAndThrowSalesForceCustomError(
        onError: ((WebClientResponseException) -> Unit)? = null
): ClientResponse {
    return awaitExchange()
            .also { response ->
                if (response.statusCode().isError) {
                    val excp = createSalesForceResponseException(response)
                    if (onError != null) onError(excp)
                    throw excp
                }
            }
}

/**
 * Creates Response Exception object while http request call specific to concur
 *
 * @param response: ClientResponse
 * @param request: HttpRequest
 * @return WebClientResponseException
 */
private suspend fun createConcurResponseException(
        response: ClientResponse,
        request: HttpRequest? = null
): WebClientResponseException {
    return DataBufferUtils
            .join(response.body(BodyExtractors.toDataBuffers()))
            .map { dataBuffer ->
                val bytes = ByteArray(dataBuffer.readableByteCount())
                dataBuffer.read(bytes)
                DataBufferUtils.release(dataBuffer)
                bytes
            }
            .defaultIfEmpty(ByteArray(0))
            .map { bodyBytes ->
                val charset = response.headers()
                        .contentType()
                        .map { it.charset }
                        .orElse(StandardCharsets.ISO_8859_1)!!
                val bodyString = bodyBytes.toString(Charset.defaultCharset())
                val statusCode = if (response.rawStatusCode() == 403 &&
                        ("token is expired" in bodyString.toLowerCase() || "incorrect credentials" in bodyString.toLowerCase())
                ) HttpStatus.UNAUTHORIZED
                else response.statusCode()
                if (HttpStatus.resolve(response.rawStatusCode()) != null)
                    WebClientResponseException.create(
                            statusCode.value(),
                            statusCode.reasonPhrase,
                            response.headers().asHttpHeaders(),
                            bodyBytes,
                            charset,
                            request
                    )
                else UnknownHttpStatusCodeException(response.rawStatusCode(), response.headers().asHttpHeaders(), bodyBytes, charset, request)
            }
            .awaitFirst()
}

/**
 * Extension function for WebClient.RequestHeadersSpec which consumes if there is any error while making http call
 *
 * @receiver WebClient.RequestHeadersSpec
 * @param onError: callback function which will be called if there is any error during http call
 * @return ClientResponse
 */
suspend fun WebClient.RequestHeadersSpec<out WebClient.RequestHeadersSpec<*>>.awaitExchangeAndThrowConcurCustomError(
        onError: ((WebClientResponseException) -> Unit)? = null
): ClientResponse {
    return awaitExchange()
            .also { response ->
                if (response.statusCode().isError) {
                    val excp = createConcurResponseException(response)
                    if (onError != null) onError(excp)
                    throw excp
                }
            }
}

