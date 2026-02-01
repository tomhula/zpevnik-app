<!DOCTYPE html>
<html lang="cs">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Můj Online Zpěvník - Seznam</title>
    <link rel="stylesheet" href="styles.css">
    <link rel="stylesheet" href="index.css">
</head>
<body>

<header>
    <h1>Můj Zpěvník</h1>
</header>

<div class="container">
    <div class="search-container">
        <input type="text" id="search-input" placeholder="Hledej název písně nebo interpreta...">
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
