package com.darglk.ktormpoc.exception

import org.springframework.http.ResponseEntity

abstract class CustomException(message: String?, val statusCode: Int) : RuntimeException(message) {
    abstract fun serializeErrors(): List<ErrorResponse>

    companion object {
        fun handleCustomException(ex: CustomException): ResponseEntity<*> {
            return ResponseEntity.status(ex.statusCode).body(mapOf(Pair("errors", ex.serializeErrors())))
        }
    }
}