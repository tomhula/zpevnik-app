package cz.tomashula.zpevnikapp

import kotlin.io.path.*


fun main()
{
    val musescoreExecutable = Path(getEnvVar(EnvVars.MUSESCORE_EXECUTABLE.name))
    if (!musescoreExecutable.exists()) 
        error("Missing musescore executable: $musescoreExecutable")
    val outputDir = Path(getEnvVar(EnvVars.OUTPUT_DIR.name))
    val repoUrl = getEnvVar(EnvVars.REPO_URL.name)
    val repoBranch = getEnvVar(EnvVars.REPO_BRANCH.name)
    
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

private fun getEnvVar(name: String): String = System.getenv(name) ?: error("Missing env var: $name")

enum class EnvVars 
{
    MUSESCORE_EXECUTABLE,
    OUTPUT_DIR,
    REPO_URL,
    REPO_BRANCH
}
