package com.darglk.ktormpoc.exception

import com.fasterxml.jackson.annotation.JsonInclude
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

data class ErrorResponse(
    val message: String?,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val field: String? = null
)