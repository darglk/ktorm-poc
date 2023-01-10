package com.darglk.ktormpoc.repository

import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val database: Database
) : UserRepository {

    private val users = database.sequenceOf(Users)

    override fun findUserByEmail(email: String): UserEntity? {
        return users.find { it.email eq email }
    }

    override fun insert(newUser: UserEntity) {
        users.add(newUser)
    }

}