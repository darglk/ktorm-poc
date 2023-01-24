package com.darglk.ktormpoc.controller

import javax.validation.constraints.NotBlank

data class UpdatePasswordRequest(
    @NotBlank
    val userId: String,
    @NotBlank
    val password: String
)
