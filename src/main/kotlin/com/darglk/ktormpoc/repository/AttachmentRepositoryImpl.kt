package com.darglk.ktormpoc.repository

import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.filter
import org.ktorm.entity.removeIf
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList
import org.ktorm.support.postgresql.bulkInsert
import org.springframework.stereotype.Repository

@Repository
class AttachmentRepositoryImpl(
    val database: Database
) : AttachmentRepository {
    val attachments = database.sequenceOf(Attachemts)

    override fun insertAll(attachmentEntities: List<AttachmentEntity>) {
        database.bulkInsert(Attachemts) {
            attachmentEntities.forEach {attachment ->
                item {
                    set(it.id, attachment.id)
                    set(it.fileKey, attachment.fileKey)
                    set(it.ticketId, attachment.ticket.id)
                }
            }
        }
    }

    override fun selectAttachments(ticketId: String) : List<AttachmentEntity> {
        return attachments.filter { it.ticketId eq ticketId }.toList()
    }

    override fun delete(ticketId: String) {
        attachments.removeIf { it.ticketId eq ticketId }
    }
}