package com.darglk.ktormpoc.config

import org.ktorm.database.Database
import org.ktorm.database.SqlDialect
import org.ktorm.logging.ConsoleLogger
import org.ktorm.logging.LogLevel
import org.ktorm.support.postgresql.PostgreSqlDialect
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class KtormConfiguration {

    @Autowired
    lateinit var dataSource: DataSource

    @Bean
    fun database(): Database {
        return Database.connectWithSpringSupport(
            dataSource = dataSource,
            logger = ConsoleLogger(threshold = LogLevel.DEBUG),
            dialect = PostgreSqlDialect(),
            // alwaysQuoteIdentifiers
            //on false: SELECT users.id AS users_id, users.email AS users_email, users."password" AS users_password, authorities.id AS authorities_id, authorities.name AS authorities_name FROM users LEFT JOIN users_authorities ON users_authorities.user_id = users.id LEFT JOIN authorities ON users_authorities.authority_id = authorities.id
            //on true:  SELECT "users"."id" AS "users_id", "users"."email" AS "users_email", "users"."password" AS "users_password", "authorities"."id" AS "authorities_id", "authorities"."name" AS "authorities_name" FROM "users" LEFT JOIN "users_authorities" ON "users_authorities"."user_id" = "users"."id" LEFT JOIN "authorities" ON "users_authorities"."authority_id" = "authorities"."id"
            alwaysQuoteIdentifiers = true,
            generateSqlInUpperCase = true
        )
    }
}