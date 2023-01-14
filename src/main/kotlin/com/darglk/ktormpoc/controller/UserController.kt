package com.darglk.ktormpoc.controller

import com.darglk.ktormpoc.exception.ValidationException
import com.darglk.ktormpoc.service.UserService
import com.darglk.ktormpoc.utils.JwtUtils
import lombok.RequiredArgsConstructor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.Errors
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
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
    fun getUsers(@RequestParam("search", required = false) search: String?): List<UsersResponse> {
        return userService.getUsers(search)
    }

//    @GetMapping(path = ["/currentuser"])
//    fun currentUser(request: HttpServletRequest): ResponseEntity<*> {
//        val token: String = request.getHeader("Authorization")
//        val parsedToken: Jws<Claims> = JwtUtils.parseToken(token)
//        return ResponseEntity.ok(
//            mapOf(Pair(
//                "currentUser", mapOf(
//                    Pair("id", parsedToken.getBody().getId()),
//                    Pair("email", parsedToken.getBody().getSubject()),
//                    Pair("iat", parsedToken.getBody().getIssuedAt().getTime())
//                )
//            )
//            )
//        )
//    }
}