package cz.tomashula.zpevnikapp

import com.github.syari.kgit.KGit
import java.nio.file.Path
import kotlin.io.path.*

class Generator(
    private val musescoreExecutable: Path,
    private val repoUrl: String,
    private val repoBranch: String,
    private val outputDir: Path
)
{
    @OptIn(ExperimentalPathApi::class)
    fun generate(): List<SongFile>
    {
        outputDir.deleteRecursively()
        val srcDir = outputDir.resolve("src").also { it.createDirectories() }
        val renderDir = outputDir.resolve("render").also { it.createDirectories() }
        cloneRepo(srcDir)
        val songs = convertDir(srcDir, renderDir)
        return songs
    }
    
    private fun cloneRepo(destination: Path)
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

        git.pull {
            remote = "origin"
            remoteBranchName = repoBranch
        }
    }

    private fun runMusescore(args: Array<String>)
    {
        val process = ProcessBuilder(musescoreExecutable.toString(), *args).start()
        process.waitFor()
    }

    private fun convert(input: Path, output: Path)
    {
        println("Converting $input to $output")
        runMusescore(arrayOf("--export-to", output.toString(), input.toString()))
    }

    private fun convertDir(inputDir: Path, outputDir: Path): List<SongFile>
    {
        val songs = mutableListOf<SongFile>()
        
        inputDir.walk().filter { it.extension == "mscz" }.forEach { srcFile ->
            convert(srcFile, outputDir.resolve(srcFile.nameWithoutExtension + ".png")) 
            val imageFiles = outputDir.walk().filter { it.extension == "png" && it.nameWithoutExtension.startsWith(srcFile.nameWithoutExtension) }.toList()
            val pdfFile = outputDir.resolve(srcFile.nameWithoutExtension + ".pdf")
            convert(srcFile, pdfFile)
            songs.add(SongFile(srcFile.nameWithoutExtension, srcFile, imageFiles, pdfFile))
        }
        
        return songs
    }
}
