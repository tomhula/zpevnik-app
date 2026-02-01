<!DOCTYPE html>
<html lang="cs">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Zpěvník</title>
    <link rel="stylesheet" href="/static/styles.css">
    <link rel="stylesheet" href="/static/song.css">
</head>
<body>

<header>
    <h1>Online Zpěvník</h1>
</header>

<div class="container">
    <div class="back-nav">
        <a href="/" class="back-link">← Zpět na seznam písniček</a>
    </div>

    <article class="song-paper">
        <!-- Vertikální galerie obrázků s notami a textem -->
        <div class="notes-gallery">
            <#list imageUrls as imageUrl>
                <img src="${imageUrl}" alt="${imageUrl} - Noty ${imageUrl?index + 1}" class="notes-image">
            </#list>
        </div>

        <!-- Sekce ke stažení -->
        <div class="download-section">
            <h3>Soubory ke stažení</h3>
            <div class="button-group">
                <a href="${musescoreUrl}" class="btn btn-musescore" download>
                    Musescore (.mscz)
                </a>
                <a href="${pdfUrl}" class="btn btn-pdf" download>
                    PDF verze
                </a>
            </div>
        </div>
    </article>
</div>

</body>
</html>
