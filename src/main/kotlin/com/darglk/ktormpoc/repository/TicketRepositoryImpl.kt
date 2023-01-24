package com.darglk.ktormpoc.repository

import org.ktorm.database.Database
import org.ktorm.dsl.count
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.groupBy
import org.ktorm.dsl.gt
import org.ktorm.dsl.having
import org.ktorm.dsl.lt
import org.ktorm.dsl.map
import org.ktorm.dsl.select
import org.ktorm.dsl.where
import org.ktorm.entity.add
import org.ktorm.entity.clear
import org.ktorm.entity.removeIf
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

    override fun selectStatusWithCount(userId: String): List<TicketStatusCountEntity> {
        return database.from(Tickets).select(Tickets.status, count(Tickets.id).aliased("count"))
            .groupBy(Tickets.status)
            .where { Tickets.userId eq userId }
            .having {
                count(Tickets.id) lt  2
            }
            .map {
                TicketStatusCountEntity(it[Tickets.status]!!, it.getInt("count"))
            }
            .toList()
    }

    override fun delete(ticketId: String) {
        tickets.removeIf { it.id eq ticketId }
    }

    override fun deleteSqlDsl(ticketId: String) {
        database.delete(Tickets) {
            it.id eq ticketId
        }
    }
}