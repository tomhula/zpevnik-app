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
    
    val generator = Generator(
        musescoreExecutable = musescoreExecutable,
        repoUrl = repoUrl,
        repoBranch = repoBranch,
        outputDir = outputDir
    )
    val songFiles = generator.generate()
    val webserver = Webserver(
        songs = songFiles,
        host = "0.0.0.0",
        port = 8080
    )
    webserver.start()
}

private fun getEnvVar(envVar: EnvVar): String = System.getenv(envVar.name) ?: error("Missing env var: ${envVar.name}")

enum class EnvVar 
{
    MUSESCORE_EXECUTABLE,
    OUTPUT_DIR,
    REPO_URL,
    REPO_BRANCH
}
