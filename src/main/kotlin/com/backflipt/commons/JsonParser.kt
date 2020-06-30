package com.backflipt.commons

import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.text.SimpleDateFormat

/**
 * Utility class for JSON Serialization/Deserialization
 */
object JsonParser {

    val mapper = jacksonObjectMapper()
            .apply { dateFormat = SimpleDateFormat("dd MM yyyy HH:mm:ss z") }


    /**
     * serializes any object to [String]
     *
     * @param value: any object
     * @return [String]
     */
    fun serialize(value: Any): String {
        return mapper.writeValueAsString(value)
    }

    /**
     * deserialize json string to [Map]
     *
     * @param string: json string
     * @return [Map]<String, Any>: deserialize json string to [Map]
     */
    fun deserialize(string: String): Map<String, Any> {
        return mapper.readValue(string)
    }


    /**
     * convert value to given Type [T]
     *
     * @param value: given input object
     * @return [T]: deserialize json string to [T]
     */
    inline fun <reified T: Any> convertValue(value: Any) = mapper.convertValue<T>(value)


}
