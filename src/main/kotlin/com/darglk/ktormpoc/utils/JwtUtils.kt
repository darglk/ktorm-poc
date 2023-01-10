package com.darglk.ktormpoc.utils

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.apache.logging.log4j.util.Strings
import java.time.Instant
import java.util.*
import javax.servlet.http.HttpServletRequest

object JwtUtils {
    fun generateToken(username: String?, userId: String?): String {
        return Jwts.builder()
            .signWith(Keys.hmacShaKeyFor("dupadupadupadupadupadupa395709375990w3590weurfhsoidhfklsjhkldfjalksjdflkajslkdfjw3u0utoueoituoisejdflkjaslkdjfoeuroiuiofjdlakjlkfh".toByteArray()), SignatureAlgorithm.HS512)
            .setIssuer("dupadupadupadupadupadupa395709375990w3590weurfhsoidhfklsjhkldfjalksjdflkajslkdfjw3u0utoueoituoisejdflkjaslkdjfoeuroiuiofjdlakjlkfh")
            .setAudience("dupadupadupadupadupadupa395709375990w3590weurfhsoidhfklsjhkldfjalksjdflkajslkdfjw3u0utoueoituoisejdflkjaslkdjfoeuroiuiofjdlakjlkfh")
            .setSubject(username)
            .setId(userId)
            .setExpiration(
                Date(
                    System.currentTimeMillis()
                            + "2121231431".toLong()
                )
            )
            .setIssuedAt(Date.from(Instant.now()))
            .compact()
    }

    fun parseToken(token: String): Jws<Claims> {
        return Jwts.parserBuilder()
            .setSigningKey("dupadupadupadupadupadupa395709375990w3590weurfhsoidhfklsjhkldfjalksjdflkajslkdfjw3u0utoueoituoisejdflkjaslkdjfoeuroiuiofjdlakjlkfh".toByteArray())
            .build()
            .parseClaimsJws(token.replace("Bearer ", Strings.EMPTY))
    }

    fun getUserIdFromToken(request: HttpServletRequest): String {
        val token = request.getHeader("Authorization")
        val claims = parseToken(token)
        return claims.body.id
    }
}