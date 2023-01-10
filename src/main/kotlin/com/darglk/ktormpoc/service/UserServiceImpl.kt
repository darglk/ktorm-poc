package com.darglk.ktormpoc.service

import com.darglk.ktormpoc.controller.CreateUserRequest
import com.darglk.ktormpoc.controller.CreateUserResponse
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
import java.util.UUID

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    @Lazy
    private val passwordEncoder: PasswordEncoder
) : UserService {

    @Throws(UsernameNotFoundException::class)
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
    override fun createUser(createUserRequest: CreateUserRequest): CreateUserResponse {
        val email = createUserRequest.email

        if (userRepository.doesEmailExist(email)) {
            throw BadRequestException("Email in use")
        }
        val newUser = UserEntity {
            this.id = UUID.randomUUID().toString()
            this.email = createUserRequest.email
            this.password = passwordEncoder.encode(createUserRequest.password)
        }

        userRepository.insert(newUser)
        return CreateUserResponse(
            newUser.id,
            newUser.email
        )
    }
}