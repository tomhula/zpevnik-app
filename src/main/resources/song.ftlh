<!DOCTYPE html>
<html lang="cs">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${name}</title>
    <link rel="stylesheet" href="${subpath}/static/styles.css">
    <link rel="stylesheet" href="${subpath}/static/song.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
</head>
<body>

<header>
    <h1>Tomův Zpěvník</h1>
</header>

<div class="container">
    <div class="back-nav">
        <a href="${subpath}/" class="back-link">
            <i class="fas fa-arrow-left"></i>
            Zpět na seznam písniček
        </a>
    </div>

    <article class="song-paper">
        <!-- Vertikální galerie obrázků s notami a textem -->
        <div class="notes-gallery">
            <#list imageUrls as imageUrl>
                <img src="${imageUrl}" alt="${imageUrl} - Noty ${imageUrl?index + 1}" class="notes-image">
            </#list>
        </div>

        <div class="audio-player">
            <p class="player-helper"><i class="fas fa-play-circle"></i> Audio</p>
            <audio controls>
                <source src="${soundUrl}" type="audio/wav">
                Váš prohlížeč nepodporuje přehrávání zvuku.
            </audio>
        </div>
        <!-- Sekce ke stažení -->
        <div class="download-section">
            <h3>Soubory ke stažení</h3>
            <div class="button-group">
                <a href="${musescoreUrl}" class="btn btn-musescore" download title="Stáhnout Musescore soubor">
                    <i class="fas fa-file-audio"></i> <span>Musescore (.mscz)</span>
                </a>
                <a href="${pdfUrl}" class="btn btn-pdf" download title="Stáhnout jako PDF">
                    <i class="fas fa-file-pdf"></i> <span>PDF</span>
                </a>
            </div>
        </div>
    </article>
</div>

</body>
</html>
