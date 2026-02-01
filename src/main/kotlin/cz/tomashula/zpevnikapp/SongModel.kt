package cz.tomashula.zpevnikapp

data class SongModel(
    val title: String,
    val imageUrls: List<String>,
    val musescoreUrl: String,
    val imagesUrl: String,
    val pdfUrl: String
)
