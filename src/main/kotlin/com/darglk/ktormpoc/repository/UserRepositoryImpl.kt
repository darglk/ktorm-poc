package com.darglk.ktormpoc.repository

import org.ktorm.database.Database
import org.ktorm.database.asIterable
import org.ktorm.dsl.asc
import org.ktorm.dsl.desc
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.inList
import org.ktorm.dsl.insert
import org.ktorm.dsl.leftJoin
import org.ktorm.dsl.limit
import org.ktorm.dsl.map
import org.ktorm.dsl.orderBy
import org.ktorm.dsl.select
import org.ktorm.dsl.update
import org.ktorm.dsl.where
import org.ktorm.dsl.whereWithConditions
import org.ktorm.entity.add
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.forEach
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.update
import org.ktorm.support.postgresql.ilike
import org.ktorm.support.postgresql.insertReturning
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    val database: Database
) : UserRepository {

    private val users = database.sequenceOf(Users)
    private val usersAuthorities = database.sequenceOf(UsersAuthorities)
    private val authorities = database.sequenceOf(Authorities)

    override fun getUsers(search: String?, page: Int, pageSize: Int): List<UserAuthoritiesEntity> {

        val userIds = database
            .from(Users)
            .select(Users.id)
            .limit(page * pageSize, pageSize)
            .whereWithConditions {
                if (search?.isEmpty() == false) {
                    it += Users.email ilike ("%$search%")
                }
            }
            .orderBy(Users.email.asc())

        return database.from(Users)
            .leftJoin(UsersAuthorities, on = UsersAuthorities.userId eq Users.id)
            .leftJoin(Authorities, on = UsersAuthorities.authorityId eq Authorities.id)
            // + dla select users.*, authorities.id, authrities.name
            .select(Users.columns + Authorities.id + Authorities.name)
            .whereWithConditions {
                it += Users.id inList userIds
                if (search?.isEmpty() == false) {
                    it += Users.email ilike ("%$search%")
                }
            }.orderBy(Users.email.asc()).map {
                UserAuthorityRow(
                    it[Users.id]!!,
                    it[Users.email]!!,
                    it[Users.password]!!,
                    it[Authorities.id],
                    it[Authorities.name]
                )
            }.fold(mutableListOf<UserAuthoritiesEntity>()) { acc, e ->
                val authorities = if (e.authorityId != null) {
                    mutableListOf(AuthorityEntity {
                        this.id = e.authorityId
                        this.name = e.authorityName!!
                    })
                } else mutableListOf()

                if (acc.map { it.id }.contains(e.id)) {
                    val entity = acc.find { it.id == e.id }!!
                    entity.authorities.addAll(authorities)
                } else {
                    acc.add(UserAuthoritiesEntity(e.id, e.email, e.password, authorities))
                }
                acc
            }.toList()
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

    override fun findUser(id: String): UserEntity? {
        return users.find { it.id eq id }
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

    override fun insertSqlDsl(newUser: UserEntity) {
        database.insertReturning(Users, Users.id) {
            set(it.id, newUser.id)
            set(it.email, newUser.email)
            set(it.password, newUser.password)
        }?.run {
            database.from(Authorities).select(Authorities.id)
                .where { Authorities.name eq "READ_AUTHORITY" }
                .map { it[Authorities.id]!! }.toList()
                .forEach { authority ->
                    database.insert(UsersAuthorities) {
                        set(it.userId, this@run)
                        set(it.authorityId, authority)
                    }
                }
        }
    }

    override fun insertSequenceApi(newUser: UserEntity) {
        users.add(newUser)
        authorities.filter { it.name inList (listOf("READ_AUTHORITY")) }
            .forEach {
                usersAuthorities.add(UserAuthorityEntity {
                    this.authority = it
                    this.user = newUser
                })
            }
    }
}