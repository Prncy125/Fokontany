package com.example.routes

import com.example.db.HabitantsTable
import com.example.model.HabitantDTO
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.habitantRoutes() {

    route("/api/habitants") {

        post {
            val dto = call.receive<HabitantDTO>()
            val id = transaction {
                HabitantsTable.insert {
                    it[foyerId] = dto.foyerId
                    it[nom] = dto.nom
                    it[prenom] = dto.prenom
                    it[sexe] = dto.sexe
                    it[dateNaissance] = dto.dateNaissance
                    it[telephone] = dto.telephone
                    it[codePaysTelephone] = dto.codePaysTelephone
                    it[estRepresentant] = dto.estRepresentant
                    it[actif] = dto.actif
                }[HabitantsTable.id]
            }
            call.respond(HttpStatusCode.Created, dto.copy(id = id))
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@put call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID invalide"))
            val dto = call.receive<HabitantDTO>()

            val updated = transaction {
                HabitantsTable.update({ HabitantsTable.id eq id }) {
                    it[foyerId] = dto.foyerId
                    it[nom] = dto.nom
                    it[prenom] = dto.prenom
                    it[sexe] = dto.sexe
                    it[dateNaissance] = dto.dateNaissance
                    it[telephone] = dto.telephone
                    it[codePaysTelephone] = dto.codePaysTelephone
                    it[estRepresentant] = dto.estRepresentant
                    it[actif] = dto.actif
                }
            }

            if (updated > 0) call.respond(HttpStatusCode.OK, dto.copy(id = id))
            else call.respond(HttpStatusCode.NotFound, mapOf("error" to "Habitant non trouve"))
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID invalide"))

            val deleted = transaction {
                HabitantsTable.deleteWhere { HabitantsTable.id eq id }
            }

            if (deleted > 0) call.respond(HttpStatusCode.OK, mapOf("message" to "Habitant supprime"))
            else call.respond(HttpStatusCode.NotFound, mapOf("error" to "Habitant non trouve"))
        }
    }
}
