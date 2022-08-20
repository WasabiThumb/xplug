
(() => {

    let topics;

    function UrlExists(url, callback)
    {
        var http = new XMLHttpRequest();
        http.open('HEAD', url);
        http.onreadystatechange = function() {
            if (this.readyState == this.DONE) {
                callback(this.status != 404);
            }
        };
        http.send();
    }

    const addClickNav = ((element, target) => {
        element.addEventListener("click", () => {
            Promise.race([
                new Promise((res) => {
                    UrlExists(target, res);
                }),
                new Promise((res) => {
                    setTimeout(() => {
                        res(true);
                    }, 1500);
                })
            ]).then((value) => {
                window.location.href = (value ? target : "/xplug/404");
            });
        });
    });

    const construct = (() => {
        const body = document.getElementById("explorerBody");
        body.innerHTML = "";

        for (let i=0; i < topics.length; i++) {
            let topic = topics[i];
            const p = document.createElement("p");
            p.classList.add("browserHeader");
            p.classList.add("browserEntry");
            p.innerText = " " + topic.title;
            body.appendChild(p);
            //
            const { entries } = topic;
            if ((entries || []).length > 0) {
                const sub = document.createElement("div");
                sub.classList.add("browserSub");
                for (let z=0; z < entries.length; z++) {
                    let entry = entries[z];
                    let p1 = document.createElement("p");
                    p1.classList.add("browserEntry");
                    if (entry.type !== 0) p1.classList.add("type" + entry.type);
                    p1.innerText = " " + entry.title;
                    p1.title = entry.id;
                    sub.appendChild(p1);
                    //
                    let { children } = entry;
                    if (!Array.isArray(children)) children = [];
                    let inherit = entry["extends"];
                    let isString = (typeof inherit) === "string";
                    if (Array.isArray(inherit) || isString) {
                        let ar = [];
                        if (isString) {
                            ar = [ inherit ];
                        } else {
                            ar = inherit;
                        }
                        for (let j=0; j < ar.length; j++) {
                            let idOf = ar[j];
                            let inheritEntry = entries.find((r) => r["id"] === idOf);
                            if (typeof inheritEntry === "object") {
                                let inheritEntryChildren = inheritEntry["children"] || [];
                                Array.prototype.push.apply(children, inheritEntryChildren);
                            }
                        }
                    }
                    children.sort((a, b) => a["title"].localeCompare(b["title"]));
                    if (children.length > 0) {
                        const section = document.createElement("div");
                        section.classList.add("browserSub");
                        for (let q=0; q < children.length; q++) {
                            const child = children[q];
                            let p2 = document.createElement("p");
                            p2.classList.add("browserEntry");
                            if (child.type !== 0) p2.classList.add("type" + child.type);
                            p2.innerText = " " + child.title;
                            p2.title = child.id;
                            section.appendChild(p2);
                            addClickNav(p2, "/xplug/" + child.id);
                        }
                        sub.appendChild(section);
                        p1.addEventListener("click", () => {
                            slideToggle(section);
                        });
                        section.addEventListener("click", (e) => {
                            e.stopImmediatePropagation();
                        })
                    } else {
                        addClickNav(p1, "/xplug/" + entry.id);
                    }
                }
                body.appendChild(sub);
                p.addEventListener("click", (e) => {
                    e.stopImmediatePropagation();
                    slideToggle(sub);
                })
            }
        }
    });

    Promise.all([
        new Promise((res) => {
            window.addEventListener("DOMContentLoaded", res);
        }),
        new Promise((res, rej) => {
            const req = new XMLHttpRequest();
            req.onreadystatechange = function () {
                if (req.readyState === 4) {
                    if (req.status >= 200 && req.status < 300) {
                        let resp = req.response;
                        let json;
                        if (req.responseType === "json") {
                            json = resp;
                        } else {
                            json = JSON.parse(resp);
                        }
                        topics = json;
                        res(json);
                    } else {
                        rej(req.status);
                    }
                }
            }
            req.onerror = rej;
            req.open("GET", "/xplug/assets/json/topics.json", true);
            req.send(null);
        })
    ]).then(construct).catch(console.error);

})();
