package com.darglk.ktormpoc.repository

import org.ktorm.database.Database
import org.ktorm.database.asIterable
import org.ktorm.dsl.Query
import org.ktorm.dsl.eq
import org.ktorm.dsl.exists
import org.ktorm.dsl.from
import org.ktorm.dsl.insert
import org.ktorm.dsl.leftJoin
import org.ktorm.dsl.map
import org.ktorm.dsl.select
import org.ktorm.dsl.update
import org.ktorm.dsl.where
import org.ktorm.dsl.whereWithConditions
import org.ktorm.entity.add
import org.ktorm.entity.any
import org.ktorm.entity.find
import org.ktorm.entity.map
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.update
import org.ktorm.expression.QueryExpression
import org.ktorm.schema.Column
import org.ktorm.support.postgresql.ilike
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    val database: Database
) : UserRepository {

    private val users = database.sequenceOf(Users)

    override fun getUsers(search: String?): List<UserAuthoritiesEntity> {

        return database.from(Users)
            .leftJoin(UsersAuthorities, on = UsersAuthorities.userId eq Users.id)
            .leftJoin(Authorities, on = UsersAuthorities.authorityId eq Authorities.id)
            // + dla select users.*, authorities.id, authrities.name
            .select(Users.columns + Authorities.id + Authorities.name)
            .whereWithConditions {
                if (search?.isEmpty() == false) {
                    Users.email ilike ("%$search%")
                }
            }.map {
                UserAuthoritiesEntity(
                    it[Users.id]!!,
                    it[Users.email]!!,
                    it[Users.password]!!,
                    if (it[Authorities.id] != null) listOf(AuthorityEntity {
                        id = it[Authorities.id]!!
                        name = it[Authorities.name]!!
                    }) else listOf()
                )
            }
    }

    override fun doesEmailExist(email: String): Boolean {
        // Sequence API
//        users.any { it.email eq email }

        // Native query
        return database.useConnection {
            val sql = """
                SELECT EXISTS (SELECT email FROM users WHERE email = ?)
            """

            it.prepareStatement(sql).use { preparedStatement ->
                preparedStatement.setString(1, email)
                preparedStatement.executeQuery().asIterable().map { it.getBoolean(1) }
            }
        }.first()
    }

    override fun updatePassword(userId: String, password: String) {
        // update via sequence api
        val user = UserEntity {
            id = userId
            this.password = password
        }

        users.update(user)
    }

    override fun updateEmail(userId: String, email: String) {
        // update query dsl
        database.update(Users) {
            set(it.email, email)
            where { it.id eq userId }
        }
    }


    override fun findUserByEmail(email: String): UserAuthoritiesEntity? {
        // sequence api używa left joinów automatycznie
        // select via query dsl
        return database.from(Users)
            .leftJoin(UsersAuthorities, on = UsersAuthorities.userId eq Users.id)
            .leftJoin(Authorities, on = UsersAuthorities.authorityId eq Authorities.id)
            .select(Users.id, Users.email, Users.password, Authorities.id, Authorities.name)
            .where { Users.email eq email }
            .map {
                UserAuthoritiesEntity(
                    it[Users.id]!!,
                    it[Users.email]!!,
                    it[Users.password]!!,
                    mutableListOf(Authorities.createEntity(it))
                )
            }
            .first()
    }

    override fun insert(newUser: UserEntity) {
        users.add(newUser)
// query dsl
//        database.insert(Users) {
//            set(it.id, newUser.id)
//            set(it.email, newUser.email)
//            set(it.password, newUser.password)
//        }
    }
}