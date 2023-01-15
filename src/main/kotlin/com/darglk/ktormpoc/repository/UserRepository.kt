package com.darglk.ktormpoc.repository

interface UserRepository {
    fun findUser(id: String): UserEntity?
    fun findUserByEmail(email: String): UserAuthoritiesEntity?
    fun insertSqlDsl(newUser: UserEntity)
    fun insertSequenceApi(newUser: UserEntity)
    fun doesEmailExist(email: String): Boolean

    fun updatePassword(userId: String, password: String)

    fun updateEmail(userId: String, email: String)
    fun getUsers(search: String?, page: Int, pageSize: Int): List<UserAuthoritiesEntity>
}