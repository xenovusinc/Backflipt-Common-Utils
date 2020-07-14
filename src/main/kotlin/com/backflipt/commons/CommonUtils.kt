package com.backflipt.commons

import java.io.UnsupportedEncodingException
import java.net.URL
import java.net.URLDecoder

/**
 * fetches the given url query Parameters
 *
 * @param url Url
 * @returns query Parameters Map
 */
@Throws(UnsupportedEncodingException::class)
fun getUrlQueryParameters(url: String): Map<String, String>? {
    val query = URL(url).query
    val pairs = query.split("&")
    return if (pairs.isNotEmpty()) {
        pairs.map {
            val idx = it.indexOf("=")
            (URLDecoder.decode(it.substring(0, idx), "UTF-8") to URLDecoder.decode(it.substring(idx + 1), "UTF-8"))
        }.toMap()
    } else null
}

/**
 * fetches the approvalKey,proxyUrl,baseUrl Specific to Oracle Connectors
 *
 * @param hubBaseUrl Url Received From Hub
 */
fun extractKeyAndBaseUrls(
        hubBaseUrl: String,
        defaultInstanceUrl: String = "",
        defaultApprovalKey: String = ""
): Triple<String, String, String> {
    val queryParametersMap = getUrlQueryParameters(hubBaseUrl)
    val urlObj = URL(hubBaseUrl)
    val proxyUrl = if (urlObj.port == -1) urlObj.protocol + "://" + urlObj.host + urlObj.path else urlObj.protocol + "://" + urlObj.host + ":" + urlObj.port + "/"
    val baseUrl = queryParametersMap?.getStringOrNull("instance") ?: defaultInstanceUrl
    val approvalTypeKey = queryParametersMap?.getStringOrNull("key") ?: defaultApprovalKey
    return Triple(approvalTypeKey, proxyUrl, baseUrl)
}

