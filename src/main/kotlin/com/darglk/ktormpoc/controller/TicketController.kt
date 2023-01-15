package com.darglk.ktormpoc.controller

import com.darglk.ktormpoc.exception.ValidationException
import com.darglk.ktormpoc.service.TicketService
import org.springframework.validation.Errors
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/api/tickets")
class TicketController(
    val ticketService: TicketService
) {

    @PostMapping("/incorrect")
    fun createTicketIncorrect(@RequestBody request:  @Valid CreateTicketRequest, errors: Errors) {
        if (errors.hasErrors()) {
            throw ValidationException(errors)
        }
        ticketService.createTicketIncorrect(request)
    }

    @PostMapping
    fun createTicket(@RequestBody request:  @Valid CreateTicketRequest, errors: Errors) {
        if (errors.hasErrors()) {
            throw ValidationException(errors)
        }
        ticketService.createTicket(request)
    }

    @GetMapping
    fun getTickets() : List<TicketResponse> {
        return ticketService.getTickets()
    }

    @DeleteMapping("/{ticketId}")
    fun deleteTicket(@PathVariable("ticketId") ticketId: String) {
        ticketService.deleteTicket(ticketId)
    }
}