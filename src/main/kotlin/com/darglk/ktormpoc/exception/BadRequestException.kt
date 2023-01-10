package com.darglk.ktormpoc.exception

import org.springframework.http.HttpStatus

class BadRequestException(message: String?) : CustomException(message, HttpStatus.BAD_REQUEST.value()) {
    override fun serializeErrors(): List<ErrorResponse> {
        return listOf(ErrorResponse(message, null))
    }
}