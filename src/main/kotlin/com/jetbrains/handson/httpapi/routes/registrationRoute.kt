package com.jetbrains.handson.httpapi

import User
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import users

fun Route.registrationRoute()
{
    route("registration")
    {
        post {
            val newUser = call.receive<User>()
            var isRepeat = false
            users.forEach()
            {
                if (it.login == newUser.login)
                    isRepeat = true
            }

            if (newUser.name_image == null)
                newUser.name_image = "profile.png"

            if (newUser.mail == null)
                newUser.mail = "not specified"

            if (!isRepeat) {
                users.add(newUser)
                call.respond(HttpStatusCode.Created)
            }else
                call.respond(HttpStatusCode(409, "AccountAlreadyExists"))
        }
    }
}