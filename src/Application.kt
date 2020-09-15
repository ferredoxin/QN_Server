package me.singleNeuron

import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.cookies.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.util.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import io.ktor.utils.io.streams.*
import kotlinx.coroutines.*
import me.singleNeuron.base.MarkdownAble
import me.singleNeuron.data.appcenter.AppCenterBuildData
import me.singleNeuron.data.github.GithubWebHookData
import me.singleNeuron.data.taichi.TaichiAddData
import me.singleNeuron.data.taichi.TaichiUploadData
import me.singleNeuron.data.LocalParam
import me.singleNeuron.data.appcenter.AppCenterCheckUpdateData
import me.singleNeuron.data.appcenter.AppCenterCrashData
import me.singleNeuron.data.appcenter.AppCenterDistributeData
import org.slf4j.Logger
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

private lateinit var dir:File
private lateinit var commitHistoryFile: File

private lateinit var localParam: LocalParam

fun main(args: Array<String>){
    dir = File("/root/QNotified_release")
    if (!dir.exists()) dir.mkdir()
    commitHistoryFile = File(dir.absolutePath+File.separator+"CommitHistory.txt")
    if (!commitHistoryFile.exists()) commitHistoryFile.createNewFile()
    try {
        val localParamFile = File("/root/QN_Server_Local.json")
        if (!localParamFile.exists()||localParamFile.readText().isBlank()) {
            localParamFile.createNewFile()
            localParam = LocalParam()
            print("请输入太极后台用户名: ")
            localParam.taichiUsername = readLine()
            print("请输入太极后台密码: ")
            localParam.taichiPassword = readLine()
            print("请输入Telegram Bot Token: ")
            localParam.botToken = readLine()
            localParamFile.writeText(Gson().toJson(localParam))
        }else {
            localParam = Gson().fromJson(localParamFile.readText(),LocalParam::class.java)
        }
        runBlocking {
            val httpClient = HttpClient()
            val response: HttpResponse = httpClient.get("https://api.telegram.org/bot${localParam.botToken}/getMe")
            println(response.readText())
            sendMessageToDevGroup("Link Start!")
            sendMessageToDevGroup(AppCenterCrashData(
                    name = "No Such Filed girlFriend Found in Object cinit",
                    reason = "决明",
                    url = "https://github.com/cinit",
                    app_version = "NaN"
            ))
            httpClient.close()
            io.ktor.server.netty.EngineMain.main(args)
        }
    }catch (e:Exception) {
        println(e)
    }
}

@KtorExperimentalAPI
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
        get("/log") {
            val logDir = File("/root/QN_Server_log")
            if (logDir.exists()&&logDir.exists()) {
                val fileName = "/root/QN_Server_log/logFile."+SimpleDateFormat("yyyy-MM-dd").format(Date())+".log"
                val logFile = File(fileName)
                if (logFile.exists()) {
                    call.respondText(logFile.readText())
                } else {
                    call.respondText("No Log")
                }
            } else {
                call.respondText("NULL")
            }
        }
        post("/webhook/github") {
            val log = call.application.environment.log
            //val string = call.receiveText()
            val data = call.receive<GithubWebHookData>()
            log.debug(data.toString())
            call.respond("success")
            if (data.repository.full_name!="ferredoxin/QNotified") {
                return@post
            }
            if (data.ref=="refs/heads/master") {
                for (commit in data.commits) {
                    commitHistoryFile.appendText("${commit.message}\n\n")
                    for (string in commit.modified) {
                        if (string=="CardMsgBlackList.json") {
                            log.debug("start downloading: ")
                            val httpClient = HttpClient()
                            val response: HttpResponse = httpClient.get("https://raw.githubusercontent.com/ferredoxin/QNotified/master/CardMsgBlackList.json")
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
            sendMessageToDevGroup(data,log)
        }
        post("/webhook/appcenter/crash") {
            val log = call.application.environment.log
            //val string = call.receiveText()
            val data = call.receive<AppCenterCrashData>()
            log.debug(data.toString())
            call.respond("")
            if(Regex("""[\W]me\.|nil\.nadph""", RegexOption.IGNORE_CASE).containsMatchIn(data.toString())) {
                sendMessageToDevGroup(data,log)
            }
        }
        post("/webhook/appcenter/distribute"){
            val log = call.application.environment.log
            //val string = call.receiveText()
            val data = call.receive<AppCenterDistributeData>()
            log.debug(data.toString())
            call.respond("")
            delay(60*1000)
            val httpClient = getHttpClientWithGson()
            val checkUpdateData = httpClient.get<AppCenterCheckUpdateData>("https://api.appcenter.ms/v0.1/public/sdk/apps/ddf4b597-1833-45dd-af28-96ca504b8123/releases/latest")
            val string = commitHistoryFile.readText()
            if (string.isNotBlank()) {
                checkUpdateData.release_notes = string
                commitHistoryFile.writeText("")
            }
            log.debug(checkUpdateData.toHtml())
            if (!checkUpdateData.download_url.isBlank()) {
                val downloadResponse: HttpResponse = httpClient.get(checkUpdateData.download_url)
                if (downloadResponse.status.isSuccess()) {
                    val fileName = "${checkUpdateData.app_name}-release ${checkUpdateData.short_version}.apk"
                    val file = File(dir.absolutePath+File.separator+fileName)
                    downloadResponse.content.copyAndClose(file.writeChannel())
                    val response:HttpResponse = httpClient.post("https://api.telegram.org/bot${localParam.botToken}/sendDocument"){
                        body = MultiPartFormDataContent(
                                formData {
                                    append("chat_id","@QNotified")
                                    append("document", InputProvider {
                                        file.inputStream().asInput()
                                    }, Headers.build {
                                        append(HttpHeaders.ContentDisposition,"filename=$fileName")
                                    })
                                    append("caption",checkUpdateData.toHtml())
                                    append("parse_mode","HTML")
                                }
                        )
                    }
                    log.debug(response.readText())
                    //uploadFileToTaichi(file,checkUpdateData.release_notes,log)
                    //sendMessageToDevGroup(checkUpdateData)
                }else {
                    log.debug("下载更新 ${checkUpdateData.short_version} 失败")
                }
            }
            httpClient.close()
        }
    }
}

suspend fun sendMessageToDevGroup(msg:String,logger:Logger?=null) {
    val httpClient = getHttpClientWithGson()
    val response: HttpResponse = httpClient.post("https://api.telegram.org/bot${localParam.botToken}/sendMessage"){
        contentType(ContentType.Application.Json)
        body = mapOf(
                "chat_id" to "-1001186899631",
                "parse_mode" to "Markdown",
                "text" to msg
        )
    }
    httpClient.close()
    if (logger!=null) logger.debug(response.readText())
    else println(response.readText())
}

suspend fun sendMessageToDevGroup(msg:MarkdownAble,logger: Logger?=null) {
    sendMessageToDevGroup(msg.toMarkdown(),logger)
    //println(msg.toMarkdown())
}

fun getHttpClientNotExpectSuccess():HttpClient {
    return HttpClient(Apache){
        expectSuccess = false
    }
}

fun getHttpClientWithGson():HttpClient {
    return HttpClient(Apache){
        expectSuccess = false
        install(JsonFeature) {
            serializer = GsonSerializer {
            }
        }
    }
}

suspend fun uploadFileToTaichi(file:File,log:String,logger:Logger) {
    val httpClient = HttpClient(Apache) {
        install(JsonFeature) {
            serializer = GsonSerializer {
            }
        }
        install(HttpCookies) {
            storage = AcceptAllCookiesStorage()
        }
    }
    val loginResponse:HttpResponse = httpClient.post("http://admin.taichi.cool/auth/login"){
        contentType(ContentType.Application.Json)
        body = mapOf(
                "username" to localParam.taichiUsername,
                "password" to localParam.taichiPassword
        )
    }
    if (loginResponse.status.isSuccess()) {
        val uploadData = httpClient.post<TaichiUploadData>("http://admin.taichi.cool/upload"){
            body = MultiPartFormDataContent(
                formData{
                    append("file", InputProvider{file.inputStream().asInput()}, Headers.build {
                        contentType(ContentType.parse("application/vnd.android.package-archive"))
                    })
                }
            )
        }
        if (uploadData.data.id!=-1) {
            httpClient.post<HttpResponse>("http://admin.taichi.cool/module/add") {
                contentType(ContentType.Application.Json)
                body = TaichiAddData.fromTaichiUploadData(uploadData,log)
            }
            logger.debug("Successful upload")
        } else {
            logger.debug("Upload failed: $uploadData")
        }
    } else {
        logger.debug("Login failed: $loginResponse\n${loginResponse.readText()}")
    }
    httpClient.close()
}