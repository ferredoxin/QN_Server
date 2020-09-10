package me.singleNeuron

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import me.singleNeuron.data.GithubWebHookData
import java.io.File

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/json/gson") {
            call.respond(mapOf("hello" to "world"))
        }
        post("/webhook/github") {
            val log = call.application.environment.log
            //val string = call.receiveText()
            val data = call.receive<GithubWebHookData>()
            log.debug(data.toString())
            call.respond("success")
            if (data.ref=="refs/heads/master") {
                for (commit in data.commits) {
                    for (string in commit.modified) {
                        if (string=="CardMsgBlackList.json") {
                            log.debug("start downloading: ")
                            val httpClient = HttpClient()
                            val response: HttpResponse = httpClient.get("https://raw.githubusercontent.com/cinit/QNotified/master/CardMsgBlackList.json")
                            log.debug("downloading: ${response.status}")
                            if (response.status.isSuccess()) {
                                val json = response.readText()
                                val file = File("/root/CardMsgBlackList.json")
                                if (!file.exists()) {
                                    if (!file.createNewFile()) {
                                        log.debug("File Create Failed")
                                        return@post
                                    }
                                }
                                file.writeText(json)
                                log.debug("File download Success")
                                log.debug(json)
                            }
                            httpClient.close()
                            return@post
                        }
                    }
                }
            }
        }
        post("/webhook/appcenter") {
            call.application.environment.log.debug(call.receiveText())
            call.respond("success")
        }
    }
}

