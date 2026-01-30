package cz.tomashula

import com.github.syari.kgit.KGit
import org.eclipse.jgit.api.Git
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createDirectory
import kotlin.io.path.createTempDirectory
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.isDirectory
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.walk

private val musescorePath = Path.of(System.getenv(EnvVars.MUSESCORE_PATH.name))
private val outputDir = Path.of(System.getenv(EnvVars.OUTPUT_DIR.name))
private val repoUrl = System.getenv(EnvVars.REPO_URL.name)
private val repoBranch = System.getenv(EnvVars.REPO_BRANCH.name)

fun main()
{
    val tempDir = createTempDirectory()
    cloneRepoRoot(repoUrl, repoBranch, tempDir)
    clearDirectory(outputDir)
    convertDir(tempDir, outputDir)
}

private fun cloneRepoRoot(repoUrl: String, repoBranch: String, destination: Path)
{
    destination.createDirectories()
    val destinationFile = destination.toFile()

    val git = KGit.init {
        setDirectory(destinationFile)
    }

    git.repository.config.setString("remote", "origin", "url", repoUrl)
    git.repository.config.setString("remote", "origin", "fetch", "+refs/heads/*:refs/remotes/origin/*")
    git.repository.config.setBoolean("core", null, "sparseCheckout", true)
    git.repository.config.save()

    val infoFolder = File(destinationFile, ".git/info")
    if (!infoFolder.exists()) infoFolder.mkdirs()
    File(destinationFile, ".git/info/sparse-checkout").writeText("/*\n!/*/")

    git.pull {
        remote = "origin"
        remoteBranchName = repoBranch
    }
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
    println("Converting $input to $output")
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
    OUTPUT_DIR,
    REPO_URL,
    REPO_BRANCH
}
