package com.darglk.ktormpoc.repository

interface TicketRepository {
    fun insert(ticketEntity: TicketEntity)
    fun select(): List<TicketEntity>
    fun selectStatusWithCount(userId: String): List<TicketStatusCountEntity>
    fun delete(ticketId: String)
    fun deleteSqlDsl(ticketId: String)
}