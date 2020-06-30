
package com.backflipt.commons

import java.util.*
/**
 * Utility class for Base64 decoding
 */
object Base64Utils {
    private val decoder = Base64.getDecoder()

    /**
     * return the decoded ByteArray
     *
     * @param s is the encoded String
     * @return decode ByteArray
     */
    fun decode(s: String): ByteArray = decoder.decode(s)
}