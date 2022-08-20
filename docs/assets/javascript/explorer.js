
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

    const construct = ((ts) => {
        const body = document.getElementById("explorerBody");
        body.innerHTML = "";

        ts = JSON.parse(JSON.stringify(ts));
        for (let i=0; i < ts.length; i++) {
            let topic = ts[i];
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
    ]).then(() => {
        construct(topics);

        const testItem = ((item, term) => {
            if ((item.title || "").toLowerCase().includes(term)) return true;
            return (item.id || "").toLowerCase().includes(term);
        });

        const filterTopics = ((searchTerm) => {
            if (searchTerm.length < 1) return topics;
            searchTerm = searchTerm.toLowerCase().replaceAll(/[:\.]/g, "/");
            let nt = [];
            let topicsCopy = JSON.parse(JSON.stringify(topics));
            for (let y=0; y < topicsCopy.length; y++) {
                let topic = topicsCopy[y];
                let entries = (Array.isArray(topic.entries) ? topic.entries : []);
                let newEntries = [];
                for (let g=0; g < entries.length; g++) {
                    let entry = entries[g];
                    if (testItem(entry, searchTerm)) {
                        newEntries.push(entry);
                    } else {
                        let children = entry.children;
                        let newChildren = [];
                        if (Array.isArray(children)) {
                            for (let u=0; u < children.length; u++) {
                                let child = children[u];
                                if (testItem(child, searchTerm)) newChildren.push(child);
                            }
                            if (newChildren.length > 0) {
                                entry.children = newChildren;
                                newEntries.push(entry);
                            }
                        }
                    }
                }
                topic.entries = newEntries;
                nt.push(topic);
            }
            return nt;
        });

        const now = (() => {
            let performance = (window.performance || performance);
            if (performance) {
                if (performance.now) {
                    return performance.now();
                }
            }
            let date = (window.Date || Date);
            if (date) {
                if (date.now) {
                    return date.now();
                } else {
                    return (new Date()).now();
                }
            }
            return 0;
        });

        let storage = (() => {
            let stor = (window.sessionStorage || sessionStorage);
            if (stor) {
                return {
                    "get": (() => stor.getItem("slow-hardware") === "true"),
                    "set": ((value) => stor.setItem("slow-hardware", value ? "true" : "false"))
                }
            } else {
                return {
                    "get": (() => {
                        let matches = (document.cookie || "").match(/slow-hardware=(true|false)/);
                        if (Array.isArray(matches)) {
                            if (matches.length > 1) return (matches[1] === "true");
                        }
                        return false;
                    }),
                    "set": ((value) => {
                        document.cookie = "slow-hardware=" + (value ? "true" : "false") + ";path=/";
                    })
                }
            }
        })();

        let isSlow = storage.get();
        const benchFilterTopics = ((searchTerm) => {
            let start = now();
            let ret = filterTopics(searchTerm);
            let elapsed = now() - start;
            isSlow = (elapsed > 5);
            storage.set(isSlow);
            return ret;
        });
        benchFilterTopics("test");

        let inp = document.getElementById("searchText");
        let btn = document.getElementById("searchBtn");

        inp.addEventListener("keyup", () => {
            if (!isSlow) construct(benchFilterTopics(inp.value));
        });

        inp.addEventListener("change", () => {
            if (!isSlow) construct(benchFilterTopics(inp.value));
        });

        btn.addEventListener("click", () => {
            construct(benchFilterTopics(inp.value));
        });
    }).catch(console.error);

})();
