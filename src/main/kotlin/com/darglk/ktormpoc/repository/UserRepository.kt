package com.darglk.ktormpoc.repository

interface UserRepository {
    fun findUserByEmail(email: String): UserEntity?
    fun insert(newUser: UserEntity)
}