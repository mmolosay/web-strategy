"use strict";

function init() {
    canvasElement.width = w;
    canvasElement.height = h;

    window.onresize = resize;

    setGameStage('waiting-players');
}

function resize() {
    w = document.body.clientWidth;
    h = document.body.clientHeight;

    canvasElement.width = w;
    canvasElement.height = h;

    gameStageChanged = true;
}

function clear() {
    if (gameStage === 'waiting-players') clearWithGradient()
}

function draw() {
    if (gameStage === 'waiting-players') {
        if (gameStageChanged) {
            c.textBaseline = 'middle';
            c.textAlign = 'center';
            c.font = '64px RobotoLight';
            gameStageChanged = false;
        }
        c.fillStyle = '#f0f8ff';
        c.fillText(waitingPlayersInfo, w / 2, h / 2);
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
    let h1 = bgGradientH + bgGradientNoise.getVal(frame);
    let h2 = lerp(h1, h / 2, 1.3);
    bgGradient = c.createLinearGradient(0, h1, w, h2);
    bgGradient.addColorStop(0, bgGradientColors.first.color);
    bgGradient.addColorStop(1, bgGradientColors.second.color);
    c.fillStyle = bgGradient;
    c.fillRect(0, 0, w, h);
}

function setGameStage(stage) {
    gameStage = stage;
    gameStageChanged = true;
}