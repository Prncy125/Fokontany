package com.example.routes

import com.example.db.ProgrammesAideTable
import com.example.model.ProgrammeAideDTO
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.programmeRoutes() {

    route("/api/programmes") {

        get {
            val programmes = transaction {
                ProgrammesAideTable.selectAll().map {
                    ProgrammeAideDTO(
                        id = it[ProgrammesAideTable.id],
                        nom = it[ProgrammesAideTable.nom],
                        description = it[ProgrammesAideTable.description],
                        dateDebut = it[ProgrammesAideTable.dateDebut]
                    )
                }
            }
            call.respond(programmes)
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID invalide"))

            val programme = transaction {
                ProgrammesAideTable.selectAll().where { ProgrammesAideTable.id eq id }.firstOrNull()
            } ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "Programme non trouve"))

            call.respond(ProgrammeAideDTO(
                id = programme[ProgrammesAideTable.id],
                nom = programme[ProgrammesAideTable.nom],
                description = programme[ProgrammesAideTable.description],
                dateDebut = programme[ProgrammesAideTable.dateDebut]
            ))
        }

        post {
            val dto = call.receive<ProgrammeAideDTO>()
            val id = transaction {
                ProgrammesAideTable.insert {
                    it[nom] = dto.nom
                    it[description] = dto.description
                    it[dateDebut] = dto.dateDebut
                }[ProgrammesAideTable.id]
            }
            call.respond(HttpStatusCode.Created, dto.copy(id = id))
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@put call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID invalide"))
            val dto = call.receive<ProgrammeAideDTO>()

            val updated = transaction {
                ProgrammesAideTable.update({ ProgrammesAideTable.id eq id }) {
                    it[nom] = dto.nom
                    it[description] = dto.description
                    it[dateDebut] = dto.dateDebut
                }
            }

            if (updated > 0) call.respond(HttpStatusCode.OK, dto.copy(id = id))
            else call.respond(HttpStatusCode.NotFound, mapOf("error" to "Programme non trouve"))
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID invalide"))

            val deleted = transaction {
                ProgrammesAideTable.deleteWhere { ProgrammesAideTable.id eq id }
            }

            if (deleted > 0) call.respond(HttpStatusCode.OK, mapOf("message" to "Programme supprime"))
            else call.respond(HttpStatusCode.NotFound, mapOf("error" to "Programme non trouve"))
        }
    }
}
