package com.darglk.ktormpoc.service

import com.darglk.ktormpoc.controller.CreateUserRequest
import com.darglk.ktormpoc.controller.CreateUserResponse
import com.darglk.ktormpoc.controller.UsersResponse
import org.springframework.security.core.userdetails.UserDetailsService

interface UserService : UserDetailsService {
    fun createUser(createUserRequest: CreateUserRequest): CreateUserResponse

    fun getUsers(search: String?): List<UsersResponse>
}