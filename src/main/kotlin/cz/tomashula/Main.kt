package cz.tomashula

import com.github.syari.kgit.KGit
import java.nio.file.Path
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.isDirectory
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.walk

private val musescorePath = Path.of(System.getenv(EnvVars.MUSESCORE_PATH.name))
private val outputDir = Path.of(System.getenv(EnvVars.OUTPUT_DIR.name))
private val repoPath = Path.of(System.getenv(EnvVars.REPO_PATH.name))

fun main()
{
    val git = KGit.open(repoPath.toFile())
    git.pull()

    clearDirectory(outputDir)

    convertDir(repoPath, outputDir)
}

private fun runMusescore(args: Array<String>)
{
    val process = ProcessBuilder(musescorePath.toString(), *args).start()
    process.waitFor()
    //TODO: Remove, debug
    process.inputStream.bufferedReader().use { println(it.readText()) }
}

private fun convert(input: Path, output: Path)
{
    runMusescore(arrayOf("--export-to", output.toString(), input.toString()))
}

private fun convertDir(inputDir: Path, outputDir: Path)
{
    inputDir.walk().filter { it.extension == "mscz" }.forEach { convert(it, outputDir.resolve(it.nameWithoutExtension + ".png")) }
}

private fun clearDirectory(directory: Path)
{
    if (!directory.exists()) 
        return
    
    if (!directory.isDirectory())
        return
    
    directory.walk().forEach { it.deleteIfExists() }
}

enum class EnvVars 
{
    MUSESCORE_PATH,
    SCORES_DIR,
    OUTPUT_DIR,
    REPO_PATH
}
