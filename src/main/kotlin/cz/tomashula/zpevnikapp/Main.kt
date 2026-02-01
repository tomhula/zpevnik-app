package cz.tomashula.zpevnikapp

import kotlin.io.path.*


fun main()
{
    val musescoreExecutable = Path(getEnvVar(EnvVar.MUSESCORE_EXECUTABLE))
    if (!musescoreExecutable.exists()) 
        error("Missing musescore executable: $musescoreExecutable")
    val outputDir = Path(getEnvVar(EnvVar.OUTPUT_DIR))
    val repoUrl = getEnvVar(EnvVar.REPO_URL)
    val repoBranch = getEnvVar(EnvVar.REPO_BRANCH)
    val host = getEnvVar(EnvVar.HOST)
    val portStr = getEnvVar(EnvVar.PORT)
    val port = portStr.toIntOrNull() ?: error("Invalid port: $portStr")
    
    val generator = Generator(
        musescoreExecutable = musescoreExecutable,
        repoUrl = repoUrl,
        repoBranch = repoBranch,
        outputDir = outputDir
    )
    val songFiles = generator.generate()
    val webserver = Webserver(
        songs = songFiles,
        host = host,
        port = port
    )
    webserver.start()
}

private fun getEnvVar(envVar: EnvVar): String = System.getenv(envVar.name) ?: error("Missing env var: ${envVar.name}")

enum class EnvVar 
{
    MUSESCORE_EXECUTABLE,
    OUTPUT_DIR,
    REPO_URL,
    REPO_BRANCH,
    HOST,
    PORT
}
