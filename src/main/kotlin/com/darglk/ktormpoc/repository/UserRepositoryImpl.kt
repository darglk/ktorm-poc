package com.darglk.ktormpoc.repository

import org.ktorm.database.Database
import org.ktorm.database.asIterable
import org.ktorm.dsl.Query
import org.ktorm.dsl.eq
import org.ktorm.dsl.exists
import org.ktorm.dsl.from
import org.ktorm.dsl.leftJoin
import org.ktorm.dsl.map
import org.ktorm.dsl.select
import org.ktorm.dsl.where
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import org.ktorm.expression.QueryExpression
import org.ktorm.schema.Column
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    val database: Database
) : UserRepository {

    private val users = database.sequenceOf(Users)

    override fun doesEmailExist(email: String): Boolean {
        return database.useConnection {
            val sql = """
                SELECT EXISTS (SELECT email FROM users WHERE email = ?)
            """

            it.prepareStatement(sql).use {
                preparedStatement ->
                preparedStatement.setString(1, email)
                preparedStatement.executeQuery().asIterable().map { it.getBoolean(1) }
            }
        }.first()
    }

    override fun findUserByEmail(email: String): UserAuthoritiesEntity? {
        return database.from(Users)
            .leftJoin(UsersAuthorities, on = UsersAuthorities.userId eq Users.id)
            .leftJoin(Authorities, on = UsersAuthorities.authorityId eq Authorities.id)
            .select(Users.id, Users.email, Users.password, Authorities.id, Authorities.name)
            .where { Users.email eq email }
            .map { UserAuthoritiesEntity(it[Users.id]!!, it[Users.email]!!, it[Users.password]!!, listOf(Authorities.createEntity(it))) }
            .first()
    }

    override fun insert(newUser: UserEntity) {
        users.add(newUser)
    }
}