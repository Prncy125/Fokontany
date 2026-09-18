package com.example

import com.example.routes.distributionRoutes
import com.example.routes.foyerRoutes
import com.example.routes.habitantRoutes
import com.example.routes.programmeRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Fokontany API - OK")
        }

        foyerRoutes()
        habitantRoutes()
        programmeRoutes()
        distributionRoutes()
    }
}
