package com.darglk.ktormpoc.service

import com.darglk.ktormpoc.controller.CreateTicketRequest
import com.darglk.ktormpoc.controller.TicketResponse
import javax.validation.Valid

interface TicketService {
    fun createTicketIncorrect(request: CreateTicketRequest)
    fun createTicket(request: @Valid CreateTicketRequest)
    fun getTickets(): List<TicketResponse>
}