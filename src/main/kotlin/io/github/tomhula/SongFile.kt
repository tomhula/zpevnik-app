package io.github.tomhula

import java.nio.file.Path

data class SongFile(
    val name: String,
    val musescore: Path,
    val images: List<Path>,
    val pdf: Path
)
