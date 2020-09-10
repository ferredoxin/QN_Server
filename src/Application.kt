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
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import me.singleNeuron.base.MarkdownAble
import me.singleNeuron.data.appcenter.AppCenterBuildData
import me.singleNeuron.data.github.GithubWebHookData
import me.singleNeuron.me.singleNeuron.data.appcenter.AppCenterCrashData
import me.singleNeuron.me.singleNeuron.data.appcenter.AppCenterDistributeData
import java.io.File
import java.util.*

private lateinit var botToken:String

fun main(args: Array<String>){
    print("请输入Telegram Bot Token: ")
    try {
        botToken = readLine()?:""
        GlobalScope.launch {
            val httpClient = HttpClient()
            val response: HttpResponse = httpClient.get("https://api.telegram.org/bot$botToken/getMe")
            println(response.readText())
            sendMessageToDevGroup("Link Start!")
            sendMessageToDevGroup(AppCenterCrashData(
                    name = "No Such Filed girlFriend Found in Object cinit",
                    reason = "决明",
                    url = "https://github.com/cinit",
                    app_version = "NaN"
            ))
        }
    }catch (e:Exception) {
        println(e)
    }
    io.ktor.server.netty.EngineMain.main(args)
}

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
        post("/webhook/appcenter/build") {
            val log = call.application.environment.log
            //val string = call.receiveText()
            val data = call.receive<AppCenterBuildData>()
            log.debug(data.toString())
            call.respond("")
            sendMessageToDevGroup(data)
        }
        post("/webhook/appcenter/crash") {
            val log = call.application.environment.log
            //val string = call.receiveText()
            val data = call.receive<AppCenterCrashData>()
            log.debug(data.toString())
            call.respond("")
            sendMessageToDevGroup(data)
        }
        post("/webhook/appcenter/distribute"){
            val log = call.application.environment.log
            //val string = call.receiveText()
            val data = call.receive<AppCenterDistributeData>()
            log.debug(data.toString())
            call.respond("")
        }
    }
}

suspend fun sendMessageToDevGroup(msg:String) {
    val httpClient = getHttpClientWithGson()
    val response: HttpResponse = httpClient.post("https://api.telegram.org/bot$botToken/sendMessage"){
        contentType(ContentType.Application.Json)
        body = mapOf(
                "chat_id" to "-1001186899631",
                "parse_mode" to "Markdown",
                "text" to msg
        )
    }
    println(response.readText())
}

suspend fun sendMessageToDevGroup(msg:MarkdownAble) {
    sendMessageToDevGroup(msg.toMarkdown())
    //println(msg.toMarkdown())
}

fun getHttpClientWithGson():HttpClient {
    return HttpClient(Apache){
        install(JsonFeature) {
            serializer = GsonSerializer {
            }
        }
    }
}