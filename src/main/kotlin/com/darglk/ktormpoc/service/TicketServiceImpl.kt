package com.darglk.ktormpoc.service

import com.darglk.ktormpoc.controller.AttachmentResponse
import com.darglk.ktormpoc.controller.CreateTicketRequest
import com.darglk.ktormpoc.controller.TicketResponse
import com.darglk.ktormpoc.repository.AttachmentEntity
import com.darglk.ktormpoc.repository.AttachmentRepository
import com.darglk.ktormpoc.repository.TicketEntity
import com.darglk.ktormpoc.repository.TicketRepository
import com.darglk.ktormpoc.repository.TicketStatus
import com.darglk.ktormpoc.repository.UserRepository
import org.ktorm.database.Database
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class TicketServiceImpl(
    val ticketRepository: TicketRepository,
    val userRepository: UserRepository,
    val attachmentRepository: AttachmentRepository,
    val database: Database
) : TicketService {

    @Transactional
    override fun createTicketIncorrect(request: CreateTicketRequest) {
        val ticketId = UUID.randomUUID().toString()

        // nie doda attachmentów
        val ticketEntity = TicketEntity {
            this.id = ticketId
            this.status = TicketStatus.CREATED
            this.title = request.title
            this.description = request.description
            this.user = userRepository.findUser("83c218e9-40bd-4def-8c0c-838f8a3f6849")!!
            this.attachments = request.attachments.map {
                AttachmentEntity {
                    this.ticket = this@TicketEntity
                    this.id = UUID.randomUUID().toString()
                    this.fileKey = this.fileKey
                }
            }
        }

        ticketRepository.insert(ticketEntity)
    }

    @Transactional
    override fun createTicket(request: CreateTicketRequest) {
        val ticketId = UUID.randomUUID().toString()
        val ticketEntity = TicketEntity {
            this.id = ticketId
            this.status = TicketStatus.CREATED
            this.title = request.title
            this.description = request.description
            this.user = userRepository.findUser("83c218e9-40bd-4def-8c0c-838f8a3f6849")!!
        }

        val attachments = request.attachments.map {
            AttachmentEntity {
                this.ticket = ticketEntity
                this.id = UUID.randomUUID().toString()
                this.fileKey = it.fileKey
            }
        }.toList()

        ticketRepository.insert(ticketEntity)
        attachmentRepository.insertAll(attachments)
    }

    // wypierdoli się na invalid cursor xd
    @Transactional(readOnly = true)
    override fun getTickets(): List<TicketResponse> {
        return ticketRepository.select().map {
            TicketResponse(it.id, it.user.id, it.description, it.status.name, it.createdAt, it.updatedAt,
                attachments = attachmentRepository.selectAttachments(it.id).map {
                    AttachmentResponse(it.id, it.fileKey)
                })
        }
    }
}