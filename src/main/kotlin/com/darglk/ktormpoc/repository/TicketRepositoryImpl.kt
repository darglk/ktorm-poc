package com.darglk.ktormpoc.repository

import org.ktorm.database.Database
import org.ktorm.entity.add
import org.ktorm.entity.clear
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList
import org.springframework.stereotype.Repository

@Repository
class TicketRepositoryImpl(
    val database: Database
) : TicketRepository {
    val tickets = database.sequenceOf(Tickets)

    override fun insert(ticketEntity: TicketEntity) {
        tickets.add(ticketEntity)
    }

    override fun select(): List<TicketEntity> {
        return tickets.toList()
    }
}