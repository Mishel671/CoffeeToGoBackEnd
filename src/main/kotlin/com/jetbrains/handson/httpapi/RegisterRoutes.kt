package com.jetbrains.handson.httpapi

import com.jetbrains.handson.httpapi.routes.catalogRoute
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import java.io.File

fun Application.registerRoutes() {

   routing {

      authenticate {
         profileSettings()
         catalogRoute()
      }

      get("catalog/getProductIcon/{category}/{icon_name}")
      {
         val ct = call.parameters["category"]    ?:   return@get call.respond(HttpStatusCode.BadRequest)
         val icon = call.parameters["icon_name"] ?:   return@get call.respond(HttpStatusCode.BadRequest)
         val file = File("photos/iconsCatalog/$ct/$icon")

         if(file.exists())
            call.respondFile(file)
         else
            call.respondText("There is no file with that name", status = HttpStatusCode.NotFound)
      }

      registrationRoute()

   }

}