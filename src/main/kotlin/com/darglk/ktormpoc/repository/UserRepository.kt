package com.darglk.ktormpoc.repository

interface UserRepository {
    fun findUserByEmail(email: String): UserAuthoritiesEntity?
    fun insert(newUser: UserEntity)
    fun doesEmailExist(email: String): Boolean
}