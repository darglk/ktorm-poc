package com.darglk.ktormpoc.utils

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.logging.log4j.util.Strings

object JSONUtils {
    private val objectMapper = ObjectMapper()
    fun <T> fromJson(json: String?, clazz: Class<T>?): T? {
        try {
            return objectMapper.readValue(json, clazz)
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
        }
        return null
    }

    fun <T> toJson(value: T): String {
        try {
            return objectMapper.writeValueAsString(value)
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
        }
        return Strings.EMPTY
    }
}