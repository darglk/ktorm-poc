package com.darglk.ktormpoc.controller

data class UsersResponse(
    val id: String,
    val email: String,
    val authorities: List<AuthorityResponse>
)
