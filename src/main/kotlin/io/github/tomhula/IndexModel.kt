package io.github.tomhula

data class IndexModel(
    val songs: List<Song>,
    val subpath: String
)
{
    data class Song(
        val name: String,
        val url: String
    )
}
