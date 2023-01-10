package com.darglk.ktormpoc.repository

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.varchar

interface UserEntity : Entity<UserEntity> {
    companion object : Entity.Factory<UserEntity>()
    var id: String
    var email: String
    var password: String
}

object Users : Table<UserEntity>("users") {
    val id = varchar("id").primaryKey().bindTo { it.id }
    val email = varchar("email").bindTo { it.email }
    val password = varchar("password").bindTo { it.password }
}