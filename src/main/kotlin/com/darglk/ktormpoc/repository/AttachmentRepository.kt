package com.darglk.ktormpoc.repository

interface AttachmentRepository {
    fun insertAll(attachmentEntities: List<AttachmentEntity>)
    fun selectAttachments(ticketId: String): List<AttachmentEntity>
    fun delete(ticketId: String)
}