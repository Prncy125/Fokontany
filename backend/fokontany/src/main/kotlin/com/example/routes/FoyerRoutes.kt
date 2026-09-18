package com.example.routes

import com.example.db.FoyersTable
import com.example.db.HabitantsTable
import com.example.model.FoyerDTO
import com.example.model.FoyerDetailDTO
import com.example.model.HabitantDTO
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.foyerRoutes() {

    route("/api/foyers") {

        get {
            val foyers = transaction {
                FoyersTable.selectAll().map {
                    FoyerDTO(
                        id = it[FoyersTable.id],
                        adresse = it[FoyersTable.adresse],
                        quartier = it[FoyersTable.quartier],
                        dateEnregistrement = it[FoyersTable.dateEnregistrement],
                        actif = it[FoyersTable.actif]
                    )
                }
            }
            call.respond(foyers)
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID invalide"))

            val foyer = transaction {
                FoyersTable.selectAll().where { FoyersTable.id eq id }.firstOrNull()
            } ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "Foyer non trouve"))

            val habitants = transaction {
                HabitantsTable.selectAll().where {
                    (HabitantsTable.foyerId eq id) and (HabitantsTable.actif eq true)
                }.map {
                    HabitantDTO(
                        id = it[HabitantsTable.id],
                        foyerId = it[HabitantsTable.foyerId],
                        nom = it[HabitantsTable.nom],
                        prenom = it[HabitantsTable.prenom],
                        sexe = it[HabitantsTable.sexe],
                        dateNaissance = it[HabitantsTable.dateNaissance],
                        telephone = it[HabitantsTable.telephone],
                        codePaysTelephone = it[HabitantsTable.codePaysTelephone],
                        estRepresentant = it[HabitantsTable.estRepresentant],
                        actif = it[HabitantsTable.actif]
                    )
                }
            }

            call.respond(FoyerDetailDTO(
                foyer = FoyerDTO(
                    id = foyer[FoyersTable.id],
                    adresse = foyer[FoyersTable.adresse],
                    quartier = foyer[FoyersTable.quartier],
                    dateEnregistrement = foyer[FoyersTable.dateEnregistrement],
                    actif = foyer[FoyersTable.actif]
                ),
                habitants = habitants
            ))
        }

        post {
            val dto = call.receive<FoyerDTO>()
            val id = transaction {
                FoyersTable.insert {
                    it[adresse] = dto.adresse
                    it[quartier] = dto.quartier
                    it[dateEnregistrement] = dto.dateEnregistrement
                    it[actif] = dto.actif
                }[FoyersTable.id]
            }
            call.respond(HttpStatusCode.Created, dto.copy(id = id))
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@put call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID invalide"))
            val dto = call.receive<FoyerDTO>()

            val updated = transaction {
                FoyersTable.update({ FoyersTable.id eq id }) {
                    it[adresse] = dto.adresse
                    it[quartier] = dto.quartier
                    it[actif] = dto.actif
                }
            }

            if (updated > 0) call.respond(HttpStatusCode.OK, dto.copy(id = id))
            else call.respond(HttpStatusCode.NotFound, mapOf("error" to "Foyer non trouve"))
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID invalide"))

            val deleted = transaction {
                FoyersTable.deleteWhere { FoyersTable.id eq id }
            }

            if (deleted > 0) call.respond(HttpStatusCode.OK, mapOf("message" to "Foyer supprime"))
            else call.respond(HttpStatusCode.NotFound, mapOf("error" to "Foyer non trouve"))
        }

        get("/{id}/habitants") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID invalide"))

            val habitants = transaction {
                HabitantsTable.selectAll().where {
                    (HabitantsTable.foyerId eq id) and (HabitantsTable.actif eq true)
                }.map {
                    HabitantDTO(
                        id = it[HabitantsTable.id],
                        foyerId = it[HabitantsTable.foyerId],
                        nom = it[HabitantsTable.nom],
                        prenom = it[HabitantsTable.prenom],
                        sexe = it[HabitantsTable.sexe],
                        dateNaissance = it[HabitantsTable.dateNaissance],
                        telephone = it[HabitantsTable.telephone],
                        codePaysTelephone = it[HabitantsTable.codePaysTelephone],
                        estRepresentant = it[HabitantsTable.estRepresentant],
                        actif = it[HabitantsTable.actif]
                    )
                }
            }
            call.respond(habitants)
        }
    }
}
