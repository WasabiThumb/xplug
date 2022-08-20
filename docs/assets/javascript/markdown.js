
const mdparse = ((md, out, setTitle) => {
    if (!((typeof setTitle) === "boolean")) setTitle = false;
    const parser = new DOMParser();
    let doc = parser.parseFromString("<root>" + md + "</root>", "text/xml");
    let { childNodes } = doc.firstElementChild;
    let lastIndex = 0;
    let firstHeader = true;
    for (let i=0; i < childNodes.length; i++) {
        let child = childNodes.item(i);
        let clearVar = true;
        if (child.nodeName === "#text") {
            clearVar = false;
            out.innerHTML += child.nodeValue.replaceAll(/(\r\n\r\n|\n\n|\r\r)/g, "<br>");
        } else if (child.nodeName === "br") {
            const br = document.createElement("br");
            out.appendChild(br);
        } else if (child.nodeName === "h1" || child.nodeName === "h2") {
            if (firstHeader && (child.nodeName === "h1")) {
                let notitle = false;
                if (child.attributes) {
                    let { attributes } = child;
                    for (let i=0; i < attributes.length; i++) {
                        let attr = attributes.item(i);
                        let localName = attr.localName.toLowerCase();
                        let val = attr.value.toLowerCase();
                        if (localName === "notitle") {
                            notitle = (val === "true");
                        }
                    }
                }
                if (!notitle) {
                    document.title = child.textContent + " | XPlug Wiki";
                    firstHeader = false;
                }
            }
            const head = document.createElement(child.nodeName);
            head.innerText = child.textContent;
            out.appendChild(head);
        } else if (child.nodeName === "code") {
            let lang = -1;
            if (child.attributes) {
                let { attributes } = child;
                for (let i=0; i < attributes.length; i++) {
                    let attr = attributes.item(i);
                    let localName = attr.localName.toLowerCase();
                    let val = attr.value;
                    if (localName === "lang") lang = val;
                }
            }
            const container = document.createElement("div");
            container.classList.add("codeContainer");
            const inner = document.createElement("code");
            inner.classList.add("prettyprint");
            if ((typeof lang) === "string") {
                inner.classList.add("lang-" + lang);
            }
            inner.innerText = child.textContent.replaceAll(" ", String.fromCharCode(160)).replace(/^(\r\n|\r|\n)+/g, "");
            container.appendChild(inner);
            out.appendChild(container);
        } else if (child.nodeName === "var" && child.nodeType === 1) {
            clearVar = false;
            let { attributes } = child;
            let index = -1;
            let name = "";
            let description = "";
            for (let i=0; i < attributes.length; i++) {
                let attr = attributes.item(i);
                let localName = attr.localName.toLowerCase();
                let val = attr.value;
                switch (localName) {
                    case "index":
                        index = val;
                        break;
                    case "name":
                        name = val;
                        break;
                    case "description":
                        description = val;
                        break;
                }
            }
            if (index === -1) {
                lastIndex++;
                index = lastIndex.toString();
            }
            let container = document.createElement("div");
            container.classList.add("varContainer");
            let create = ((name, value) => {
                let el = document.createElement("div");
                el.classList.add(name);
                el.innerText = value;
                container.appendChild(el);
            });
            create("index", index);
            create("name", name);
            create("desc", description);
            out.appendChild(container);
        } else if (child.nodeName === "a") {
            const anchor = document.createElement("a");
            anchor.innerText = child.textContent;
            if (child.attributes) {
                let { attributes } = child;
                for (let i=0; i < attributes.length; i++) {
                    let attr = attributes.item(i);
                    anchor.setAttribute(attr.localName, attr.value);
                }
            }
            out.appendChild(anchor);
        }
        if (clearVar) lastIndex = 0;
    }
    out.innerHTML += `<div style='display: block; height: 5vh'></div>`;
});