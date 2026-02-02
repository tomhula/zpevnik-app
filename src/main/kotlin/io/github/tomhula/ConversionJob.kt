package io.github.tomhula

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class ConversionJob(val tasks: List<Task>)
{
    @Serializable
    data class Task(
        @SerialName("in")
        val input: String, 
        @SerialName("out")
        val output: String
    )
}
