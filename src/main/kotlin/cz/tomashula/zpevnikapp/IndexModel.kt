package cz.tomashula.zpevnikapp

data class IndexModel(
    val songs: List<Song>
)
{
    data class Song(
        val name: String,
        val url: String
    )
}
