"use strict";

function init() {
    canvasElement.width = w;
    canvasElement.height = h;

    window.onresize = resize;
}

function resize() {
    w = document.body.clientWidth;
    h = document.body.clientHeight;

    canvasElement.width = w;
    canvasElement.height = h;
}

function clear() {
    if (gameStage === 'waiting-players') clearWithGradient()
}

function draw() {
    if (gameStage === 'waiting-players') {

    }
}

function loop() {
    requestAnimationFrame(loop);
    clear();
    draw();
}

//==================================================//

function clearWithGradient() {
    c.fillStyle = bgGradient;
    c.fillRect(0, 0, w, h);
}