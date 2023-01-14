package com.darglk.ktormpoc.security

import com.darglk.ktormpoc.repository.UserRepository
import com.darglk.ktormpoc.service.UserService
import lombok.RequiredArgsConstructor
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@EnableWebSecurity
class SecurityConfiguration(
    private val userDetailsService: UserService,
    private val userRepository: UserRepository
) : WebSecurityConfigurerAdapter() {

    override fun configure(web: WebSecurity?) {
        web?.ignoring()
            ?.antMatchers("/api/users/signup/*")
            ?.antMatchers("/api/users/users")
            ?.antMatchers("/api/tickets/**")
    }

    override fun configure(http: HttpSecurity?) {
        http?.let {
            it.cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/users/signin", "/api/users/users")
                .permitAll().and()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .addFilter(JwtAuthorizationFilter(authenticationManager()))
                .addFilter(JwtAuthenticationFilter(authenticationManager(), userRepository));
        }
    }

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth?.userDetailsService(userDetailsService)

    }

    @Bean
    fun passwordEncoder() : PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManagerLoad(): AuthenticationManager? {
        return authenticationManager()
    }
}