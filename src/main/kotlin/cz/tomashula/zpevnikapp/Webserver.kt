package cz.tomashula.zpevnikapp

import freemarker.cache.ClassTemplateLoader
import io.ktor.http.HttpHeaders
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.freemarker.FreeMarker
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.http.content.staticResources
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.response.respondPath
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlin.io.path.name

class Webserver(
    private val songs: List<SongFile>,
    private val port: Int,
    private val host: String
)
{
    fun start()
    {
        println("Starting webserver on $host:$port")
        println("Songs:")
        songs.forEach { println(it) }
        
        embeddedServer(
            factory = Netty,
            host = host,
            port = port,
            module = { module() }
        ).start(wait = true)
    }

    private fun Application.module()
    {
        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "/")
        }

        routing {
            staticResources("/static", "/static", index = null)

            get("/") {
                call.respond(FreeMarkerContent("index.ftlh", songs.toIndexModel()))
            }

            get("/song/{name}") {
                val name = call.parameters["name"] ?: return@get
                val songFile = songs.find { it.name == name } ?: return@get

                val model = SongModel(
                    imageUrls = songFile.images.indices.map { index -> "/song/${songFile.name}/image/$index" },
                    musescoreUrl = "/song/${songFile.name}/musescore",
                    imagesUrl = "/",
                    pdfUrl = "/song/${songFile.name}/pdf"
                )
                call.respond(FreeMarkerContent("song.ftlh", model))
            }
            
            get("/song/{name}/musescore") {
                val name = call.parameters["name"] ?: return@get
                val songFile = songs.find { it.name == name } ?: return@get
                call.response.headers.append(HttpHeaders.ContentDisposition, "attachment; filename=\"${songFile.musescore.name}\"")
                call.respondPath(songFile.musescore)
            }
            
            get("/song/{name}/pdf") {
                val name = call.parameters["name"] ?: return@get
                val songFile = songs.find { it.name == name } ?: return@get
                call.response.headers.append(HttpHeaders.ContentDisposition, "attachment; filename=\"${songFile.pdf.name}\"")
                call.respondPath(songFile.pdf)
            }
            
            get("/song/{name}/image/{index}") {
                val name = call.parameters["name"] ?: return@get
                val index = call.parameters["index"]?.toIntOrNull() ?: return@get
                val songFile = songs.find { it.name == name } ?: return@get
                val imageFile = songFile.images.getOrNull(index) ?: return@get
                call.response.headers.append(HttpHeaders.ContentDisposition, "attachment; filename=\"${imageFile.name}\"")
                call.respondPath(imageFile)
            }
        }
    }
    
    private fun List<SongFile>.toIndexModel() = IndexModel(
        songs = map { it.toIndexModelSong() }
    )
    
    private fun SongFile.toIndexModelSong() = IndexModel.Song(name, "song/$name")
}
