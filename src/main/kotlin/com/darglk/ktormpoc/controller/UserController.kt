package com.darglk.ktormpoc.controller

import com.darglk.ktormpoc.exception.ValidationException
import com.darglk.ktormpoc.service.UserService
import com.darglk.ktormpoc.utils.JwtUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.Errors
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid
import kotlin.math.absoluteValue


@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {
    @PostMapping(path = ["/signup/dsl"])
    fun signupDsl(
        @RequestBody request: @Valid CreateUserRequest,
        errors: Errors,
        res: HttpServletResponse
    ): ResponseEntity<*> {
        if (errors.hasErrors()) {
            throw ValidationException(errors)
        }
        val response = userService.createUserDsl(request)
        val token = JwtUtils.generateToken(response.email, response.id)
        res.setHeader("Authorization", "Bearer $token")
        return ResponseEntity.status(HttpStatus.CREATED).body<Any>(response)
    }

    @PostMapping(path = ["/signup/seq"])
    fun signupSeq(
        @RequestBody request: @Valid CreateUserRequest,
        errors: Errors,
        res: HttpServletResponse
    ): ResponseEntity<*> {
        if (errors.hasErrors()) {
            throw ValidationException(errors)
        }
        val response = userService.createUserSeq(request)
        val token = JwtUtils.generateToken(response.email, response.id)
        res.setHeader("Authorization", "Bearer $token")
        return ResponseEntity.status(HttpStatus.CREATED).body<Any>(response)
    }

    @GetMapping("/users")
    fun getUsers(
        @RequestParam("search", required = false) search: String?,
        @RequestParam("page", defaultValue = "0", required = false) page: Int, @RequestParam("pageSize", defaultValue = "1", required = false) pageSize: Int
    ): List<UsersResponse> {
        return userService.getUsers(search, page.absoluteValue, pageSize.absoluteValue)
    }

    @GetMapping("/exist")
    fun doesUserExist(@RequestParam("email") email: String) {
        userService.doesUserExist(email)
    }

    @PutMapping("/password")
    fun updatePassword(@RequestBody request: UpdatePasswordRequest) {
        userService.updatePassword(request)
    }

    @PutMapping("/email")
    fun updateEmail(@RequestBody request: UpdateEmailRequest) {
        userService.updateEmail(request)
    }
}