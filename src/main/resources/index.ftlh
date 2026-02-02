<!DOCTYPE html>
<html lang="cs">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tomův Zpěvník</title>
    <link rel="stylesheet" href="${subpath}/static/styles.css">
    <link rel="stylesheet" href="${subpath}/static/index.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
</head>
<body>

<header>
    <h1>Tomův Zpěvník</h1>
</header>

<div class="container">
    <div class="toolbar">
        <div class="search-container">
            <i class="fas fa-search search-icon"></i>
            <input type="text" id="search-input" placeholder="Hledej název písně...">
        </div>
    </div>

    <ul id="song-list" class="song-list">
        <#list songs as song>
            <a href="${song.url}" class="song-link">
                <li class="song-item">
                    <div class="song-info">
                        <h3>${song.name}</h3>
                    </div>
                    <span class="arrow">›</span>
                </li>
            </a>
        <#else>
            <li class="song-item">Žádná píseň nebyla nalezena.</li>
        </#list>
    </ul>

    <#if sourceUrl?has_content>
        <footer class="index-footer">
            <a href="${sourceUrl}" target="_blank" class="source-link">
                <i class="fab fa-github"></i> Repositář pro zpěvník
            </a>
        </footer>
    </#if>
</div>

<script>
    const searchInput = document.getElementById('search-input');
    const songList = document.getElementById('song-list');
    const items = songList.getElementsByTagName('a');

    searchInput.addEventListener('input', (e) => {
        const term = e.target.value.toLowerCase();
        for (let i = 0; i < items.length; i++) {
            const title = items[i].querySelector('h3').innerText.toLowerCase();
            // Since IndexModel doesn't have artist, we only search by title here
            if (title.includes(term)) {
                items[i].style.display = "";
            } else {
                items[i].style.display = "none";
            }
        }
    });
</script>

</body>
</html>
