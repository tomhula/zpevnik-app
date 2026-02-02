package io.github.tomhula

import io.github.oshai.kotlinlogging.KotlinLogging
import com.github.syari.kgit.KGit
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import java.nio.file.Path
import kotlin.io.path.*

class Generator(
    private val musescoreExecutable: String,
    private val repoUrl: String,
    private val repoBranch: String,
    private val outputDir: Path
)
{
    private val logger = KotlinLogging.logger {}

    @OptIn(ExperimentalPathApi::class)
    fun generate(): List<SongFile>
    {
        if (outputDir.exists())
            outputDir.forEach { it.deleteRecursively() }
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
        logger.info { "Running $musescoreExecutable ${args.joinToString(" ")}" }
        val process = ProcessBuilder(musescoreExecutable, *args).start()
        process.waitFor()
    }

    private fun convert(input: Path, output: Path)
    {
        logger.info { "Converting $input to $output" }
        runMusescore(arrayOf("--export-to", output.toString(), input.toString()))
    }
    
    @OptIn(ExperimentalSerializationApi::class)
    private fun convert(job: ConversionJob)
    {
        val jobFile = createTempFile(suffix = ".json")
        val json = Json.encodeToString(job)
        logger.info { "Running a conversion job: $json" }
        jobFile.writeText(json)
        runMusescore(arrayOf("--job", jobFile.toString()))
    }

    private fun convertDir(inputDir: Path, outputDir: Path): List<SongFile>
    {
        val songs = mutableListOf<SongFile>()
        val tasks = mutableSetOf<ConversionJob.Task>()
        val srcFiles = inputDir.walk().filter { it.extension == "mscz" }.toList()
        
        srcFiles.forEach { srcFile ->
            val svg = outputDir.resolve(srcFile.nameWithoutExtension + ".svg")
            val pdf = outputDir.resolve(srcFile.nameWithoutExtension + ".pdf")
            tasks.add(ConversionJob.Task(input = srcFile.toString(), output = svg.toString()))
            tasks.add(ConversionJob.Task(input = srcFile.toString(), output = pdf.toString()))
        }
        
        convert(ConversionJob(tasks))
        
        srcFiles.forEach { srcFile -> 
            val pdf = outputDir.resolve(srcFile.nameWithoutExtension + ".pdf")
            val svgs = outputDir.walk().filter { it.extension == "svg" && it.nameWithoutExtension.startsWith(srcFile.nameWithoutExtension) }.toList()
            songs.add(SongFile(name = srcFile.nameWithoutExtension, musescore = srcFile, pdf = pdf, images = svgs))
        }
        
        return songs
    }
}
