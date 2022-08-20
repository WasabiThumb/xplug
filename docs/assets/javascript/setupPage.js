
(() => {

    let page = `
    <!DOCTYPE html>
    <html lang="en">
        <head>
            <title>XPlug Wiki</title>
            <link rel="apple-touch-icon" sizes="180x180" href="/xplug/apple-touch-icon.png">
            <link rel="icon" type="image/png" sizes="32x32" href="/xplug/favicon-32x32.png">
            <link rel="icon" type="image/png" sizes="16x16" hreff="/xplug/favicon-16x16.png">
            <link rel="manifest" href="/xplug/site.webmanifest">
            <link rel="mask-icon" href="/xplug/safari-pinned-tab.svg" color="#067c62">
            <meta name="msapplication-TileColor" content="#024737">
            <meta name="theme-color" content="#024737">
            <link rel="stylesheet" href="/xplug/assets/stylesheets/main.css">
            <script src="https://cdn.jsdelivr.net/gh/google/code-prettify@master/loader/run_prettify.js"></script>
            <script src="https://cdn.jsdelivr.net/gh/google/code-prettify@master/src/lang-lua.js"></script>
            <script src="https://cdn.jsdelivr.net/gh/hadialqattan/no-darkreader/nodarkreader.min.js"></script>
            <script src="/xplug/assets/javascript/effects.js"></script>
            <script src="/xplug/assets/javascript/markdown.js"></script>
            <script src="/xplug/assets/javascript/explorer.js"></script>
        </head>
        <body>
            <header>
                <img id="logo" class="logo" src="/xplug/assets/images/banner.png" alt="XPlug">
                <p id="projectDesc">
                    &bull;
                    A LUA platform for Spigot servers
                </p>
                <nav>
                    <a href="https://github.com/WasabiThumb/xplug" target="_blank">
                        <img src="/xplug/assets/images/gh.svg" alt="GitHub Page" title="GitHub Page">
                    </a>
                    <a href="https://github.com/WasabiThumb/xplug/releases" target="_blank">
                        <img src="/xplug/assets/images/download.svg" alt="Download" title="Download" style="height: 0.85em">
                    </a>
                    <a href="/">
                        <img src="/xplug/assets/images/home.svg" alt="Home" title="Home">
                    </a>
                </nav>
            </header>
            <main>
            </main>
            <div class="explorer">
                <div class="searchBar">
                    <label for="searchText" aria-hidden="true"></label> <!-- makes my IDE happy -->
                    <input id="searchText" type="text" placeholder="Search" autocomplete="off">
                    <button id="searchBtn">
                        <img src="/xplug/assets/images/search.svg" alt="🔎">
                    </button>
                </div>
                <div class="body" id="explorerBody">
                    Loading
                </div>
            </div>
        </body>
    </html>
    `;

    const el = document.getElementById("body");
    const md = el.textContent;
    document.write(page);
    window.addEventListener("DOMContentLoaded", () => {
        mdparse(md, document.querySelector("main"), true);
    });

})();