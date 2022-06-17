package com.jetbrains.handson.httpapi

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import users
import java.io.File

fun Route.profileSettings()
{
    route("profileSettings")
    {

        //Получение фото с профиля пользователя
        get("getIcon")
        {
            val principal = call.principal<JWTPrincipal>()
            val login = principal!!.payload.getClaim("login").asString()
            val foundUser = users.find { it.login == login} ?:  return@get call.respondText(
                "incorrect username or password",
                status = HttpStatusCode.NotFound
            )
            val file = File("photos/profiles/${foundUser.name_image}")

            if(file.exists())
                call.respondFile(file)
            else
                call.respondText("There is no file with that name", status = HttpStatusCode.NotFound)
        }

        changeProfile()

    }
}

fun Route.changeProfile()
{
    route("change")
    {

        put("mail/{new_mail}")
        {
            val principal = call.principal<JWTPrincipal>()
            val login = principal!!.payload.getClaim("login").asString()
            val mail = call.parameters["new_mail"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            var isFound = false

            users.forEach()
            {
                if (it.login == login) {
                    it.mail = mail
                    isFound = true
                    call.respond(HttpStatusCode.OK)
                }
            }

            if (!isFound)
                call.respond(HttpStatusCode.NotFound)
        }


        put("icon/{new_icon}")
        {
            val principal = call.principal<JWTPrincipal>()
            val login = principal!!.payload.getClaim("login").asString()
            val icon = call.parameters["new_icon"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            var isFound = false

            users.forEach()
            {
                if (it.login == login) {
                    it.name_image = icon
                    isFound = true
                    call.respond(HttpStatusCode.OK)
                }
            }

            if (!isFound)
                call.respond(HttpStatusCode.NotFound)
        }
    }
}



// нахуй не надо но как пример чтобы потом заюзать
fun Route.UploadIcons()
{
    route("upload")
    {
        post("profile")
        {
            var fileName: String
            val multipartData = call.receiveMultipart()

            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        fileName = part.originalFileName as String
                        val fileBytes = part.streamProvider().readBytes()
                        File("photos/profiles/$fileName").writeBytes(fileBytes)
                    }
                }
            }
        }

        post("icons/{category}")
        {
            var fileName: String
            val multipartData = call.receiveMultipart()

            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        fileName = part.originalFileName as String
                        val fileBytes = part.streamProvider().readBytes()
                        File("photos/iconsCatalog/category/$fileName").writeBytes(fileBytes)
                    }
                }
            }
        }
    }
}