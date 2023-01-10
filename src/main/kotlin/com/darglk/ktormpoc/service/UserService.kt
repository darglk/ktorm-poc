package com.darglk.ktormpoc.service

import com.darglk.ktormpoc.controller.CreateUserRequest
import com.darglk.ktormpoc.controller.CreateUserResponse
import org.springframework.security.core.userdetails.UserDetailsService

interface UserService : UserDetailsService {
    fun createUser(createUserRequest: CreateUserRequest): CreateUserResponse
}