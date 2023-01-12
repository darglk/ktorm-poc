package com.darglk.ktormpoc.controller

import com.fasterxml.jackson.annotation.JsonInclude

data class AuthorityResponse(
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val id: String,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val name: String
)
