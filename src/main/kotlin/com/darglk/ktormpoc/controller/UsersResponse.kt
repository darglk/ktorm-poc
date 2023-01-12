package com.darglk.ktormpoc.controller

import com.fasterxml.jackson.annotation.JsonInclude

data class UsersResponse(
    val id: String,
    val email: String,
    val authorities: MutableList<AuthorityResponse>
)
