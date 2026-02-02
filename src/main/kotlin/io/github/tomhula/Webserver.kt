package io.github.tomhula

import io.github.oshai.kotlinlogging.KotlinLogging
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
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.IgnoreTrailingSlash
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlin.io.path.name

class Webserver(
    private var songs: List<SongFile>,
    private val port: Int,
    private val host: String,
    private val onRefresh: () -> List<SongFile> = { songs },
    subpath: String
)
{
    private val logger = KotlinLogging.logger {}
    private val normalizedSubpath = subpath.trim('/').let { if (it.isEmpty()) "" else "/$it" }

    fun start()
    {
        logger.info { "Songs: ${songs.joinToString { it.name }}" }
        
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
        install(IgnoreTrailingSlash)

        routing {
            route(normalizedSubpath) {
                staticResources("/static", "/static", index = null)

                get {
                    call.respond(FreeMarkerContent("index.ftlh", songs.toIndexModel()))
                }
                
                post("/refresh") {
                    songs = onRefresh()
                    logger.info { "Refreshed songs: ${songs.joinToString { it.name }}" }
                    call.respondRedirect("/$normalizedSubpath")
                }
                
                route("/song/{name}") {
                    get {
                        val name = call.parameters["name"] ?: return@get
                        val songFile = songs.find { it.name == name } ?: return@get

                        val model = SongModel(
                            imageUrls = songFile.images.indices.map { index -> "$normalizedSubpath/song/${songFile.name}/image/$index" },
                            musescoreUrl = "$normalizedSubpath/song/${songFile.name}/musescore",
                            pdfUrl = "$normalizedSubpath/song/${songFile.name}/pdf",
                            subpath = normalizedSubpath
                        )
                        call.respond(FreeMarkerContent("song.ftlh", model))
                    }

                    get("/musescore") {
                        val name = call.parameters["name"] ?: return@get
                        val songFile = songs.find { it.name == name } ?: return@get
                        call.response.headers.append(HttpHeaders.ContentDisposition, "attachment; filename=\"${songFile.musescore.name}\"")
                        call.respondPath(songFile.musescore)
                    }

                    get("/pdf") {
                        val name = call.parameters["name"] ?: return@get
                        val songFile = songs.find { it.name == name } ?: return@get
                        call.response.headers.append(HttpHeaders.ContentDisposition, "attachment; filename=\"${songFile.pdf.name}\"")
                        call.respondPath(songFile.pdf)
                    }

                    get("/image/{index}") {
                        val name = call.parameters["name"] ?: return@get
                        val index = call.parameters["index"]?.toIntOrNull() ?: return@get
                        val songFile = songs.find { it.name == name } ?: return@get
                        val imageFile = songFile.images.getOrNull(index) ?: return@get
                        call.response.headers.append(HttpHeaders.ContentDisposition, "attachment; filename=\"${imageFile.name}\"")
                        call.respondPath(imageFile)
                    }
                }
            }
        }
    }
    
    private fun List<SongFile>.toIndexModel() = IndexModel(
        songs = map { it.toIndexModelSong() },
        subpath = normalizedSubpath
    )
    
    private fun SongFile.toIndexModelSong() = IndexModel.Song(name, "$normalizedSubpath/song/$name")
}
