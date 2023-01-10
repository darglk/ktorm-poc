package com.darglk.ktormpoc.controller

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class UserLoginRequestModel(
    val email: @Email(message = "email must be valid") @NotBlank String,
    val password: @NotBlank @Size(
        min = 4,
        max = 20,
        message = "Password must be between 4 and 20 characters"
    ) String
)
