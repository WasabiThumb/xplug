
const slideToggle = (() => {

    let update = (() => {
        const logo = document.getElementById("logo");
        const desc = document.getElementById("projectDesc");
        let ratio = window.innerWidth / window.innerHeight;

        if (ratio < 0.57) {
            logo.src = "/xplug/assets/images/icon.png";
            desc.style.display = "none";
        } else if (ratio < 1.2) {
            logo.src = "/xplug/assets/images/banner.png";
            desc.style.display = "none";
        } else {
            logo.src = "/xplug/assets/images/banner.png";
            desc.style.display = "";
        }
    });

    window.addEventListener("load", () => {
        window.addEventListener("resize", update);
        update();
    });

    const slideOperations = {};

    let interpolate = ((value) => {
        // x^2 ( 3 - 2x )
        // I designed this interpolation myself but im sure someone else
        // has thought of it before.
        return Math.pow(value, 2) * (3 - 2 * value);
    });

    let slideFrame;
    slideFrame = (() => {
        let keys = Array.from(Object.keys(slideOperations));
        let now = window.performance.now();
        for (let i=0; i < keys.length; i++) {
            let key = keys[i];
            let data = slideOperations[key];
            //
            let pc = Math.min((now - data.startTime) / data.duration, 1);
            let eased = interpolate(pc);
            let h = (data.to * eased) + (data.from * (1 - eased));
            data.element.style.height = h + "px";
            if (pc >= 1) {
                delete slideOperations[key];
                if (data.unlockHeightWhenDone) data.element.style.height = "";
                if (data.clearOverflowWhenDone) data.element.style.overflowY = "";
            } else {
                slideOperations[key] = data;
            }
        }
        window.requestAnimationFrame(slideFrame);
    });

    let indexHead = -1;
    const slide = ((element, expand) => {
        element.style.height = "";
        let fullHeight = element.offsetHeight;
        element.style.height = (expand ? "0px" : "");
        //
        if (indexHead === -1) window.requestAnimationFrame(slideFrame);
        let index;
        if (element.hasAttribute("data-slide-index")) {
            index = parseInt(element.getAttribute("data-slide-index"));
        } else {
            indexHead++;
            index = indexHead;
            element.setAttribute("data-slide-index", indexHead.toString());
        }
        //
        element.style.overflowY = "hidden";
        element.style.display = "";
        let now = window.performance.now();
        let from = (expand ? 0 : fullHeight);
        let to = (expand ? fullHeight : 0);
        let duration = 500;
        let cur = slideOperations[index];
        if (typeof cur === "object") {
            let cpc = Math.min((now - cur.startTime) / cur.duration, 1);
            let cEased = interpolate(cpc);
            let target = (cur.to * cEased) + (cur.from * (1 - cEased));
            let rate = Math.abs(duration / (to - from));
            duration = rate * Math.abs(target - to);
            from = target;
        }
        slideOperations[index] = {
            element,
            from,
            to,
            duration,
            startTime: now,
            unlockHeightWhenDone: expand,
            clearOverflowWhenDone: expand
        }
    });

    const slideToggle = ((element) => {
        let expanded = true;
        if (element.hasAttribute("data-slide-expanded")) {
            let attr = (element.getAttribute("data-slide-expanded") || "").toLowerCase();
            if (attr === "false") expanded = false;
            if (attr === "0") expanded = false;
        }
        if (expanded) {
            slide(element, false);
            element.setAttribute("data-slide-expanded", "false");
        } else {
            slide(element, true);
            element.setAttribute("data-slide-expanded", "true");
        }
    });

    return slideToggle;

})();
