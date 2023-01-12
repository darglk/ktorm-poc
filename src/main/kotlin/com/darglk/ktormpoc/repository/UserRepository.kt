package com.darglk.ktormpoc.repository

interface UserRepository {
    fun findUserByEmail(email: String): UserAuthoritiesEntity?
    fun insert(newUser: UserEntity)
    fun doesEmailExist(email: String): Boolean

    fun updatePassword(userId: String, password: String)

    fun updateEmail(userId: String, email: String)
    fun getUsers(search: String?): List<UserAuthoritiesEntity>
}