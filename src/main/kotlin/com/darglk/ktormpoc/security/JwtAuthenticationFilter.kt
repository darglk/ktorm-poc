package com.darglk.ktormpoc.security

import com.darglk.ktormpoc.controller.UserLoginRequestModel
import com.darglk.ktormpoc.exception.ErrorResponse
import com.darglk.ktormpoc.exception.InvalidCredentialsException
import com.darglk.ktormpoc.repository.UserRepository
import com.darglk.ktormpoc.utils.JSONUtils
import com.darglk.ktormpoc.utils.JwtUtils
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import lombok.SneakyThrows
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.ConstraintViolation
import javax.validation.Validation

class JwtAuthenticationFilter(
    private val authManager: AuthenticationManager,
    private val userRepository: UserRepository
) : UsernamePasswordAuthenticationFilter() {

    init {
        setFilterProcessesUrl("/api/users/signin")
    }

    @SneakyThrows
    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        return try {
            val creds: UserLoginRequestModel = ObjectMapper().registerModule(KotlinModule()).readValue(
                request.inputStream,
                UserLoginRequestModel::class.java
            )
            validateLoginCredentials(creds)
            val authenticationToken = UsernamePasswordAuthenticationToken(
                creds.email, creds.password
            )
            authManager.authenticate(authenticationToken)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun validateLoginCredentials(creds: UserLoginRequestModel) {
        val validatorFactory = Validation.buildDefaultValidatorFactory()
        val validator = validatorFactory.validator
        val validationResult: Set<ConstraintViolation<UserLoginRequestModel>> =
            validator.validate<UserLoginRequestModel>(creds)
        if (!validationResult.isEmpty()) {
            throw InvalidCredentialsException("Invalid login credentials", validationResult)
        }
    }

    override fun successfulAuthentication(
        request: HttpServletRequest, response: HttpServletResponse,
        filterChain: FilterChain, authentication: Authentication
    ) {
        val user = userRepository.findUserByEmail(authentication.name)!!
        val token: String = JwtUtils.generateToken(user.email, user.id)
        response.addHeader("Authorization", "Bearer $token")
    }

    @Throws(IOException::class)
    override fun unsuccessfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        failed: AuthenticationException
    ) {
        response.contentType = "application/json"
        if (failed is InvalidCredentialsException) {
            response.writer
                .write(
                    JSONUtils
                        .toJson(mapOf(Pair("errors", (failed as InvalidCredentialsException).serializeErrors())))
                )
            response.status = HttpStatus.BAD_REQUEST.value()
        } else {
            response.writer.write(
                JSONUtils
                    .toJson(
                        mapOf(Pair(
                            "errors",
                            listOf(ErrorResponse("Invalid login credentials", null)))
                        )
                    )
            )
            response.status = HttpStatus.UNAUTHORIZED.value()
        }
        SecurityContextHolder.clearContext()
        rememberMeServices.loginFail(request, response)
    }
}