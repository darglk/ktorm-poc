package com.darglk.ktormpoc.exception

import org.springframework.http.HttpStatus

class NotAuthorizedException : CustomException("Unauthorized", HttpStatus.UNAUTHORIZED.value()) {
    override fun serializeErrors(): List<ErrorResponse> {
        return listOf(ErrorResponse("Not authorized", null))
    }
}