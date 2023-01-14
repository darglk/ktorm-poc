package com.darglk.ktormpoc.controller

import javax.validation.constraints.NotBlank

data class CreateTicketRequest(
    @NotBlank
    val title: String,
    @NotBlank
    val description: String,
    val attachments: List<AttachmentRequest>
)
