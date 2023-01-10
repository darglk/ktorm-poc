package com.darglk.ktormpoc.exception

import com.darglk.ktormpoc.controller.UserLoginRequestModel
import lombok.Getter
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import javax.validation.ConstraintViolation

@Getter
class InvalidCredentialsException(msg: String, validationResult: Set<ConstraintViolation<UserLoginRequestModel>>) :
    AuthenticationException(msg) {
    private val statusCode: Int = HttpStatus.BAD_REQUEST.value()
    private val validationResult: Set<ConstraintViolation<UserLoginRequestModel>>

    init {
        this.validationResult = validationResult
    }

    fun serializeErrors(): List<ErrorResponse> {
        return validationResult.stream()
            .map { result: ConstraintViolation<UserLoginRequestModel> ->
                ErrorResponse(
                    result.message,
                    result.propertyPath.toString()
                )
            }.toList()
    }
}