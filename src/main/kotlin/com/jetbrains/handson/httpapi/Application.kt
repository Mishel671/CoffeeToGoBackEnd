package com.jetbrains.handson.httpapi

import RespondUser
import UserAuthorization
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import users

var args = listOf<String>().toTypedArray()

fun main(): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    install(ContentNegotiation)
    {
        gson()
    }

    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val audience = environment.config.property("jwt.audience").getString()
    val myRealm = environment.config.property("jwt.realm").getString()

    install(Authentication) {
        jwt {
            realm = myRealm

            verifier(
                JWT
                    .require(Algorithm.HMAC256(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build()
            )

            validate { credential ->
                if (credential.payload.getClaim("login").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }

    registerRoutes()

    routing {

        get("/") {
            call.respond("Работает")
        }

        get("allLogins")
        {
            call.respond(users)
        }
        route("login")
        {
            post{
                val respondUser: RespondUser
                val user = call.receive<UserAuthorization>()
                val foundUser = users.find { it.login == user.login && it.password == user.password }

                if(foundUser != null) {
                    val token = JWT.create()
                        .withAudience(audience)
                        .withIssuer(issuer)
                        .withClaim("login", foundUser.login)
//                .withExpiresAt(Date(System.currentTimeMillis() + 60000))
                        .sign(Algorithm.HMAC256(secret))

                    respondUser = RespondUser(foundUser.login,token,foundUser.mail as String,foundUser.name_image as String)
                    call.respond(respondUser)
                }else
                    call.respondText(
                        "incorrect username or password",
                        status = HttpStatusCode.Unauthorized
                    )
            }
        }
    }
}