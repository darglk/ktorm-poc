package com.darglk.ktormpoc.controller

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class CreateUserRequest(
    val email: @NotBlank @Email String,
    val password: @NotBlank @Size(min = 4, max = 20) String
)