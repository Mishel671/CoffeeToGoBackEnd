package com.jetbrains.handson.httpapi.routes

import com.jetbrains.handson.httpapi.data.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.io.File

fun Route.catalogRoute() {
    route("catalog")
    {
        post("addCategory/{name_ct}")
        {
            val newCategory = call.parameters["name_ct"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val newItem = call.receive<ItemFromCatalog>()
            catalog.put(newCategory, mutableListOf(newItem))
            call.respond(HttpStatusCode.Created)
        }


        //Добавление нового объекта в категорию
        post("add/{category}")
        {
            val category = call.parameters["category"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val newItem = call.receive<ItemFromCatalog>()
            var isRepeat = false

            catalog[category]?.forEach()
            {
                if (it.item == newItem.item)
                    isRepeat = true
            }
            if (!isRepeat) {
                catalog[category]?.add(newItem)
                call.respond(HttpStatusCode.Created)
            } else {
                call.respond(HttpStatusCode.NotModified)
            }
        }

        get("all")
        {
            call.respond(catalog)
        }

        get("drinks{drink_type}")
        {
            val drinkType =
                call.request.queryParameters["drink_type"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            when (drinkType) {
                "tea" -> call.respond(tea)
                "coffee" -> call.respond(coffee)
            }
        }

        get("categories")
        {
            call.respond(categories)
        }
    }
}