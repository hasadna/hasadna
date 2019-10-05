// initialize Audio context on page load.
let AudioContext = window.webkitAudioContext || window.AudioContext;
let audioContext = new AudioContext();
let source = null;
// A variable which holds the current table cell under the touch point.
let currentCellUnderTouchPoint;

function processData() {
    let input = document.getElementById("textInput").value;
    let lines = input.split("\n");
    let table = document.createElement("table");
    table.style.width = "100%";
    table.style.height = "70%";
    table.setAttribute("border", "1");
    let line;
    for (line of lines) {
        let tr = document.createElement("tr");
        let values = line.split("\t");
        fillRow(values, tr);
        table.appendChild(tr);
    }
    let oldTable = document.getElementById("tableContainer").firstChild;
    if (oldTable != null) {
        document.getElementById("tableContainer").removeChild(oldTable);
    }
    document.getElementById("tableContainer").appendChild(table);
    addOnClickAndTouchSoundToTable();
}

function fillRow(values, currentTr) {
    let myValue;
    for (myValue of values) {
        let td = document.createElement("TD");
        td.appendChild(document.createTextNode(myValue));
        currentTr.appendChild(td);
    }
}

function addOnClickAndTouchSoundToTable() {
    let element;
    for (element of document.getElementsByTagName("td")) {
        element.addEventListener("click", startSoundPlaybackOnClick);
        element.addEventListener("touchstart", startSoundPlaybackOnClick);
        element.addEventListener("touchmove", onCellChange);
        element.addEventListener("touchleave", stopSoundPlayback);
        element.addEventListener("touchcancel", stopSoundPlayback);
    }
}

function startSoundPlaybackOnClick(event) {
    currentCellUnderTouchPoint = event.currentTarget;
    let valueCalledOn = event.currentTarget.firstChild.data;
    event.preventDefault();
    startSoundPlayback();
}

function startSoundPlayback() {
    if (audioContext.state == "suspended") {
        audioContext.resume();
    }
    let request = new XMLHttpRequest();
    request.open("get", "assets/beep_digital.mp3", true);
    request.responseType = "arraybuffer";
    request.onload = function () {
        let data = request.response;
        playSoundFromData(data);
    };
    request.send();
}

function playSoundFromData(data) {
    source = audioContext.createBufferSource();
    audioContext.decodeAudioData(data, function (buffer) {
        source.buffer = buffer;
        source.connect(audioContext.destination);
        playSoundFromBufferSource();
    });
}

function playSoundFromBufferSource() {
    source.start(audioContext.currentTime);
}

function stopSoundPlayback(event) {
    event.preventDefault();
    source.stop(audioContext.currentTime);
}

function onCellChange(event) {
    try {
        let changedTouch = event.changedTouches[0];
        let elementUnderTouch = document.elementFromPoint(changedTouch.clientX, changedTouch.clientY);
        //document.getElementById("log").innerHTML += "touch moved" + "<br>";
        if (elementUnderTouch == currentCellUnderTouchPoint) {
            //document.getElementById("log").innerHTML += "still in the same cell" + "<br>";
            return;
        }
        if (elementUnderTouch == null || elementUnderTouch.tagName != "TD") {
            //document.getElementById("log").innerHTML += "out of a table cell" + "<br>";
            return;
        }
        currentCellUnderTouchPoint = elementUnderTouch;
        stopSoundPlayback(event);
        //document.getElementById("log").innerHTML += "stopped sound" + "<br>";
        startSoundPlayback();
        //document.getElementById("log").innerHTML += "started sound" + "<br>";
        event.stopPropagation();
    } catch (e) {
        document.getElementById("log").innerHTML += e + "<br>";
    }
}