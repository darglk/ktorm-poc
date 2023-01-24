package com.darglk.ktormpoc.controller

import javax.validation.constraints.NotBlank

data class UpdateEmailRequest(
    @NotBlank
    val userId: String,
    @NotBlank
    val email: String
)
