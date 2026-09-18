package com.example.routes

import com.example.db.DistributionsAideTable
import com.example.model.DistributionAideDTO
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.distributionRoutes() {

    route("/api/distributions") {

        get {
            val distributions = transaction {
                DistributionsAideTable.selectAll().map {
                    DistributionAideDTO(
                        id = it[DistributionsAideTable.id],
                        programmeAideId = it[DistributionsAideTable.programmeAideId],
                        foyerId = it[DistributionsAideTable.foyerId],
                        representantHabitantId = it[DistributionsAideTable.representantHabitantId],
                        dateDistribution = it[DistributionsAideTable.dateDistribution],
                        montant = it[DistributionsAideTable.montant],
                        quantite = it[DistributionsAideTable.quantite],
                        statut = it[DistributionsAideTable.statut]
                    )
                }
            }
            call.respond(distributions)
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID invalide"))

            val dist = transaction {
                DistributionsAideTable.selectAll().where { DistributionsAideTable.id eq id }.firstOrNull()
            } ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "Distribution non trouvee"))

            call.respond(DistributionAideDTO(
                id = dist[DistributionsAideTable.id],
                programmeAideId = dist[DistributionsAideTable.programmeAideId],
                foyerId = dist[DistributionsAideTable.foyerId],
                representantHabitantId = dist[DistributionsAideTable.representantHabitantId],
                dateDistribution = dist[DistributionsAideTable.dateDistribution],
                montant = dist[DistributionsAideTable.montant],
                quantite = dist[DistributionsAideTable.quantite],
                statut = dist[DistributionsAideTable.statut]
            ))
        }

        get("/foyer/{foyerId}") {
            val foyerId = call.parameters["foyerId"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID invalide"))

            val distributions = transaction {
                DistributionsAideTable.selectAll()
                    .where { DistributionsAideTable.foyerId eq foyerId }
                    .orderBy(DistributionsAideTable.dateDistribution, SortOrder.DESC)
                    .map {
                        DistributionAideDTO(
                            id = it[DistributionsAideTable.id],
                            programmeAideId = it[DistributionsAideTable.programmeAideId],
                            foyerId = it[DistributionsAideTable.foyerId],
                            representantHabitantId = it[DistributionsAideTable.representantHabitantId],
                            dateDistribution = it[DistributionsAideTable.dateDistribution],
                            montant = it[DistributionsAideTable.montant],
                            quantite = it[DistributionsAideTable.quantite],
                            statut = it[DistributionsAideTable.statut]
                        )
                    }
            }
            call.respond(distributions)
        }

        post {
            val dto = call.receive<DistributionAideDTO>()

            val exists = transaction {
                DistributionsAideTable.selectAll().where {
                    (DistributionsAideTable.programmeAideId eq dto.programmeAideId) and
                            (DistributionsAideTable.foyerId eq dto.foyerId) and
                            (DistributionsAideTable.statut neq "ANNULEE")
                }.count()
            }

            if (exists > 0) {
                call.respond(
                    HttpStatusCode.Conflict,
                    mapOf("error" to "Ce foyer a deja recu cette aide.")
                )
                return@post
            }

            val id = transaction {
                DistributionsAideTable.insert {
                    it[programmeAideId] = dto.programmeAideId
                    it[foyerId] = dto.foyerId
                    it[representantHabitantId] = dto.representantHabitantId
                    it[dateDistribution] = dto.dateDistribution
                    it[montant] = dto.montant
                    it[quantite] = dto.quantite
                    it[statut] = dto.statut
                }[DistributionsAideTable.id]
            }
            call.respond(HttpStatusCode.Created, dto.copy(id = id))
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@put call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID invalide"))
            val dto = call.receive<DistributionAideDTO>()

            val updated = transaction {
                DistributionsAideTable.update({ DistributionsAideTable.id eq id }) {
                    it[statut] = dto.statut
                }
            }

            if (updated > 0) call.respond(HttpStatusCode.OK, dto.copy(id = id))
            else call.respond(HttpStatusCode.NotFound, mapOf("error" to "Distribution non trouvee"))
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID invalide"))

            val deleted = transaction {
                DistributionsAideTable.deleteWhere { DistributionsAideTable.id eq id }
            }

            if (deleted > 0) call.respond(HttpStatusCode.OK, mapOf("message" to "Distribution supprimee"))
            else call.respond(HttpStatusCode.NotFound, mapOf("error" to "Distribution non trouvee"))
        }
    }
}
