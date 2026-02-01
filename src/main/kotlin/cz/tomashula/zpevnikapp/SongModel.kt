package cz.tomashula.zpevnikapp

data class SongModel(
    val imageUrls: List<String>,
    val musescoreUrl: String,
    val pdfUrl: String,
    val subpath: String
)
