package io.github.tomhula

data class SongModel(
    val name: String,
    val imageUrls: List<String>,
    val musescoreUrl: String,
    val pdfUrl: String,
    val soundUrl: String,
    val subpath: String
)
