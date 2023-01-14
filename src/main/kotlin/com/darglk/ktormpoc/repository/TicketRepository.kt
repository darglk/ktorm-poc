package com.darglk.ktormpoc.repository

interface TicketRepository {
    fun insert(ticketEntity: TicketEntity)
    fun select(): List<TicketEntity>
}