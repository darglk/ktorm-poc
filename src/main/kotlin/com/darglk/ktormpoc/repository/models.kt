package com.darglk.ktormpoc.repository

import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.map
import org.ktorm.dsl.select
import org.ktorm.dsl.where
import org.ktorm.entity.Entity
import org.ktorm.entity.filter
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList
import org.ktorm.schema.Table
import org.ktorm.schema.enum
import org.ktorm.schema.text
import org.ktorm.schema.timestamp
import org.ktorm.schema.varchar
import org.springframework.security.core.GrantedAuthority
import java.time.Instant

enum class UserStatus {
    ENABLED, BLOCKED
}

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
    var status: UserStatus
    var createdAt: Instant
    var updatedAt: Instant
    var authorities: List<AuthorityEntity>
}

object Users : Table<UserEntity>("users") {
    val id = varchar("id").primaryKey().bindTo { it.id }
    val email = varchar("email").bindTo { it.email }
    val password = varchar("password").bindTo { it.password }
    val createdAt = timestamp("created_at").bindTo { it.createdAt }
    val updatedAt = timestamp("updated_at").bindTo { it.updatedAt }
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

interface AttachmentEntity : Entity<AttachmentEntity> {
    companion object : Entity.Factory<AttachmentEntity>()
    var id: String
    var ticket: TicketEntity
    var fileKey: String
    var createdAt: Instant
    var updatedAt: Instant
}

object Attachemts : Table<AttachmentEntity>("attachments") {
    val id = varchar("id").primaryKey().bindTo { it.id }
    val ticketId = varchar("ticket_id").references(Tickets) { it.ticket }
    val fileKey = varchar("file_key").bindTo { it.fileKey }
    val createdAt = timestamp("created_at").bindTo { it.createdAt }
    val updatedAt = timestamp("updated_at").bindTo { it.updatedAt }
}

enum class TicketStatus {
    CREATED, IN_PROGRESS, COMPLETED
}

interface TicketEntity : Entity<TicketEntity> {
    companion object : Entity.Factory<TicketEntity>()
    var id: String
    var user: UserEntity
    var title: String
    var description: String
    var status: TicketStatus
    var createdAt: Instant
    var updatedAt: Instant
    var attachments: List<AttachmentEntity>
    // też nie działa:
//    fun getAttachments(database: Database): List<AttachmentEntity> {
//        return database.sequenceOf(Attachemts).filter { it.ticketId eq this.id }.toList()
//    }
}

object Tickets : Table<TicketEntity>("tickets") {
    val id = varchar("id").primaryKey().bindTo { it.id }
    val userId = varchar("user_id").references(Users) { it.user }
    val title = varchar("title").bindTo { it.title }
    val description = text("description").bindTo { it.description }
    val status = enum<TicketStatus>("status").bindTo { it.status }
    val createdAt = timestamp("created_at").bindTo { it.createdAt }
    val updatedAt = timestamp("updated_at").bindTo { it.updatedAt }
}