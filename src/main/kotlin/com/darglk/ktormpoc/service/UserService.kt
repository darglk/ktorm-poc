package com.darglk.ktormpoc.service

import com.darglk.ktormpoc.controller.CreateUserRequest
import com.darglk.ktormpoc.controller.CreateUserResponse
import com.darglk.ktormpoc.controller.UpdateEmailRequest
import com.darglk.ktormpoc.controller.UpdatePasswordRequest
import com.darglk.ktormpoc.controller.UsersResponse
import org.springframework.security.core.userdetails.UserDetailsService

interface UserService : UserDetailsService {
    fun getUsers(search: String?, page: Int, pageSize: Int): List<UsersResponse>
    fun createUserSeq(createUserRequest: CreateUserRequest): CreateUserResponse
    fun createUserDsl(createUserRequest: CreateUserRequest): CreateUserResponse
    fun doesUserExist(email: String)
    fun updatePassword(request: UpdatePasswordRequest)
    fun updateEmail(request: UpdateEmailRequest)
}