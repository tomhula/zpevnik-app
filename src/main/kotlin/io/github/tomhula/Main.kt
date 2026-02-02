package io.github.tomhula

import kotlin.io.path.*


fun main()
{
    val musescoreExecutable = requireEnvVar(EnvVar.MUSESCORE_EXECUTABLE)
    val outputDir = Path(requireEnvVar(EnvVar.OUTPUT_DIR))
    val repoUrl = requireEnvVar(EnvVar.REPO_URL)
    val repoBranch = requireEnvVar(EnvVar.REPO_BRANCH)
    val host = requireEnvVar(EnvVar.HOST)
    val portStr = requireEnvVar(EnvVar.PORT)
    val port = portStr.toIntOrNull() ?: error("Invalid port: $portStr")
    val subpath = getEnvVar(EnvVar.SUBPATH) ?: ""
    
    val generator = Generator(
        musescoreExecutable = musescoreExecutable,
        repoUrl = repoUrl,
        repoBranch = repoBranch,
        outputDir = outputDir
    )
    val songFiles = generator.generate().sortedBy { it.name }
    val webserver = Webserver(
        songs = songFiles,
        port = port,
        host = host,
        subpath = subpath
    )
    webserver.start()
}

private fun requireEnvVar(envVar: EnvVar) = getEnvVar(envVar) ?: error("Missing env var: ${envVar.name}")

private fun getEnvVar(envVar: EnvVar): String? = System.getenv(envVar.name)

enum class EnvVar 
{
    MUSESCORE_EXECUTABLE,
    OUTPUT_DIR,
    REPO_URL,
    REPO_BRANCH,
    HOST,
    PORT,
    SUBPATH
}
