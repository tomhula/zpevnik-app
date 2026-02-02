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
        
        val srcDir = outputDir.resolve("src")
        val renderDir = outputDir.resolve("render")
        srcDir.createDirectories()
        renderDir.createDirectories()
        
        cloneRepo(srcDir)
        
        return convertDir(srcDir, renderDir)
    }

    private fun cloneRepo(destination: Path)
    {
        destination.createDirectories()
        
        logger.info { "Cloning $repoUrl to $destination" }
        
        val git = KGit.init {
            setDirectory(destination.toFile())
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
        
        val exitCode = process.waitFor()
        
        if (exitCode != 0)
            throw RuntimeException("MuseScore exited with code $exitCode")
    }

    private fun convert(input: Path, output: Path)
    {
        logger.info { "Converting $input to $output" }
        runMusescore(arrayOf("--export-to", output.toString(), input.toString()))
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun convert(job: ConversionJob)
    {
        // https://github.com/musescore/MuseScore/issues/31998
        val sortedTasks = job.tasks.sortedByDescending { it.output.substringAfterLast('.') }
        val newJob = ConversionJob(sortedTasks)
        val jobFile = createTempFile(suffix = ".json")
        try
        {
            val json = Json.encodeToString(newJob)
            logger.info { "Running a conversion job: $json" }
            jobFile.writeText(json)
            runMusescore(arrayOf("--job", jobFile.toString()))
        }
        finally
        {
            jobFile.deleteIfExists()
        }
    }

    @OptIn(ExperimentalPathApi::class)
    private fun convertDir(inputDir: Path, outputDir: Path): List<SongFile>
    {
        val srcFiles = inputDir.walk().filter { it.extension == "mscz" }.toList()

        val tasks = srcFiles.flatMap { srcFile ->
            val svg = outputDir.resolve("${srcFile.nameWithoutExtension}.svg")
            val pdf = outputDir.resolve("${srcFile.nameWithoutExtension}.pdf")
            val wav = outputDir.resolve("${srcFile.nameWithoutExtension}.wav")
            listOf(
                ConversionJob.Task(input = srcFile.toString(), output = svg.toString()),
                ConversionJob.Task(input = srcFile.toString(), output = pdf.toString()),
                ConversionJob.Task(input = srcFile.toString(), output = wav.toString())
            )
        }

        convert(ConversionJob(tasks))

        val allSvgs = outputDir.walk().filter { it.extension == "svg" }.toList()
        return srcFiles.map { srcFile ->
            val pdf = outputDir.resolve("${srcFile.nameWithoutExtension}.pdf")
            val wav = outputDir.resolve("${srcFile.nameWithoutExtension}.wav")
            val svgs = allSvgs.filter { it.nameWithoutExtension.startsWith(srcFile.nameWithoutExtension) }
            
            SongFile(name = srcFile.nameWithoutExtension, musescore = srcFile, images = svgs, sound = wav, pdf = pdf)
        }
    }
}
