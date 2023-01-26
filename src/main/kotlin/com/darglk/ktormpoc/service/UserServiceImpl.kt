package com.darglk.ktormpoc.service

import com.darglk.ktormpoc.controller.AuthorityResponse
import com.darglk.ktormpoc.controller.CreateUserRequest
import com.darglk.ktormpoc.controller.CreateUserResponse
import com.darglk.ktormpoc.controller.UpdateEmailRequest
import com.darglk.ktormpoc.controller.UpdatePasswordRequest
import com.darglk.ktormpoc.controller.UsersResponse
import com.darglk.ktormpoc.exception.BadRequestException
import com.darglk.ktormpoc.repository.UserEntity
import com.darglk.ktormpoc.repository.UserRepository
import org.springframework.context.annotation.Lazy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    @Lazy
    private val passwordEncoder: PasswordEncoder
) : UserService {

    @Transactional(readOnly = true)
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findUserByEmail(username) ?: throw UsernameNotFoundException("user not found")
        return User(
            user.email,
            user.password,
            true,
            true,
            true,
            true, user.authorities
        )
    }

    @Transactional
    override fun createUserSeq(createUserRequest: CreateUserRequest): CreateUserResponse {
        return createUser(createUserRequest, false)
    }

    @Transactional
    override fun createUserDsl(createUserRequest: CreateUserRequest): CreateUserResponse {
        return createUser(createUserRequest, true)
    }

    @Transactional(readOnly = true)
    override fun doesUserExist(email: String) {
        if (userRepository.doesEmailExist(email)) {
            throw BadRequestException("user exists")
        }
    }

    @Transactional
    override fun updatePassword(request: UpdatePasswordRequest) {
        userRepository.updatePassword(request.userId, request.password)
    }

    @Transactional
    override fun updateEmail(request: UpdateEmailRequest) {
        userRepository.updateEmail(request.userId, request.email)
    }

    @Transactional(readOnly = true)
    override fun getUsers(search: String?, page: Int, pageSize: Int): List<UsersResponse> {
        return userRepository.getUsers(search, page, pageSize).map {
            UsersResponse(
                it.id, it.email,
                it.authorities.map { AuthorityResponse(it.id, it.name) }.toList()
            )
        }.toList()
    }

    private fun createUser(createUserRequest: CreateUserRequest, queryDsl: Boolean): CreateUserResponse {
        val email = createUserRequest.email

        if (userRepository.doesEmailExist(email)) {
            throw BadRequestException("Email in use")
        }

        val newUser = UserEntity {
            this.id = UUID.randomUUID().toString()
            this.email = createUserRequest.email
            this.password = passwordEncoder.encode(createUserRequest.password)
        }

        if (queryDsl) {
            userRepository.insertSqlDsl(newUser)
        } else {
            userRepository.insertSequenceApi(newUser)
        }
        return CreateUserResponse(
            newUser.id,
            newUser.email
        )
    }
}