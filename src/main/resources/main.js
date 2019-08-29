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
    frame++;
}

//==================================================//

function clearWithGradient() {
    let h1 = (h / 2) + bgGradientNoise.getVal(frame);
    let h2 = (h / 2) - bgGradientNoise.getVal(frame);
    console.log("h1: " + h1 + "; h2: " + h2);
    bgGradient = c.createLinearGradient(0, h1, w, h2);
    bgGradient.addColorStop(0, bgGradientColors.first.color);
    bgGradient.addColorStop(1, bgGradientColors.second.color);
    c.fillStyle = bgGradient;
    c.fillRect(0, 0, w, h);
}