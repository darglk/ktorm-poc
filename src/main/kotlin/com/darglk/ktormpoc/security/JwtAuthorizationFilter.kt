package com.darglk.ktormpoc.security

import com.darglk.ktormpoc.exception.CustomException
import com.darglk.ktormpoc.exception.ErrorResponse
import com.darglk.ktormpoc.exception.NotAuthorizedException
import com.darglk.ktormpoc.utils.JSONUtils
import com.darglk.ktormpoc.utils.JwtUtils
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jws
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.SignatureException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthorizationFilter(authenticationManager: AuthenticationManager?) :
    BasicAuthenticationFilter(authenticationManager) {
    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        response.contentType = "application/json"
        val authentication: UsernamePasswordAuthenticationToken? = try {
            getAuthentication(request)
        } catch (error: CustomException) {
            response.status = error.statusCode
            response.writer.write(JSONUtils.toJson(mapOf(Pair("errors", error.serializeErrors()))))
            return
        } catch (exception: Exception) {
            response.status = HttpStatus.BAD_REQUEST.value()
            response.writer
                .write(
                    JSONUtils.toJson(
                        mapOf(
                            Pair(
                                "errors",
                                listOf(ErrorResponse("Something went wrong", null))
                            )
                        )
                    )
                )
            return
        }
        SecurityContextHolder.getContext().authentication = authentication
        filterChain.doFilter(request, response)
    }

    private fun getAuthentication(request: HttpServletRequest): UsernamePasswordAuthenticationToken {
        val token: String = request.getHeader("Authorization")
        if (token.startsWith("Bearer ")) {
            try {
                val parsedToken: Jws<Claims> = JwtUtils.parseToken(token)
                val username: String = parsedToken.body.subject
                if (username.isNotEmpty()) {
                    return UsernamePasswordAuthenticationToken(username, null, emptyList<GrantedAuthority>())
                }
            } catch (exception: ExpiredJwtException) {
                log.warn("Request to parse expired JWT : {} failed : {}", token, exception.message)
            } catch (exception: UnsupportedJwtException) {
                log.warn("Request to parse unsupported JWT : {} failed : {}", token, exception.message)
            } catch (exception: MalformedJwtException) {
                log.warn("Request to parse invalid JWT : {} failed : {}", token, exception.message)
            } catch (exception: SignatureException) {
                log.warn("Request to parse JWT with invalid signature : {} failed : {}", token, exception.message)
            } catch (exception: IllegalArgumentException) {
                log.warn("Request to parse empty or null JWT : {} failed : {}", token, exception.message)
            }
        }
        throw NotAuthorizedException()
    }

    companion object {
        private val log = LoggerFactory.getLogger(JwtAuthorizationFilter::class.java)
    }
}