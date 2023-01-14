package com.darglk.ktormpoc.controller

import javax.validation.constraints.NotBlank

data class AttachmentRequest(
    @NotBlank
    val fileKey: String
)
