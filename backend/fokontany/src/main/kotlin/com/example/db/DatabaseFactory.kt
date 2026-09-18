package com.example.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        Database.connect(
            url = "jdbc:mysql://localhost:3306/fokontany_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
            driver = "com.mysql.cj.jdbc.Driver",
            user = "root",
            password = "root"
        )

        transaction {
            SchemaUtils.create(
                FoyersTable,
                HabitantsTable,
                ProgrammesAideTable,
                DistributionsAideTable
            )
        }
    }
}
