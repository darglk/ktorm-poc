package com.darglk.ktormpoc.repository

import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.inList
import org.ktorm.entity.Entity
import org.ktorm.entity.filter
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList
import org.ktorm.schema.Table
import org.ktorm.schema.varchar
import org.springframework.security.core.GrantedAuthority

data class UserAuthoritiesEntity(
    val id: String,
    val email: String,
    val password: String,
    val authorities: List<AuthorityEntity>
)

interface UserEntity : Entity<UserEntity> {
    // bez compaiona nie można tworzyć instancji i przypisywać wartości do pól
    companion object : Entity.Factory<UserEntity>()
    var id: String
    var email: String
    var password: String
    var authorities: List<AuthorityEntity>
}

object Users : Table<UserEntity>("users") {
    val id = varchar("id").primaryKey().bindTo { it.id }
    val email = varchar("email").bindTo { it.email }
    val password = varchar("password").bindTo { it.password }
}

interface AuthorityEntity : GrantedAuthority, Entity<AuthorityEntity> {
    companion object : Entity.Factory<AuthorityEntity>()
    var id: String
    var name: String
}

object Authorities : Table<AuthorityEntity>("authorities") {
    val id = varchar("id").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }
}

interface UserAuthorityEntity : Entity<UserAuthorityEntity> {
    companion object : Entity.Factory<UserAuthorityEntity>()
    var user: UserEntity
    var authority: AuthorityEntity
}

object UsersAuthorities : Table<UserAuthorityEntity>("users_authorities") {
    val userId = varchar("user_id").references(Users) { it.user }
    val authorityId = varchar("authority_id").references(Authorities) { it.authority }
}
