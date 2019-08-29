"use strict";

function init() {
    canvasElement.width = w;
    canvasElement.height = h;

    window.onresize = resize;

    setGameStage(gameStages.waitingPlayers);
}

function resize() {
    w = document.body.clientWidth;
    h = document.body.clientHeight;

    canvasElement.width = w;
    canvasElement.height = h;

    gameStageChanged = true;
}

function clear() {
    if (gameStage === gameStages.waitingPlayers) clearWithGradient()
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
        c.fillText(waitingPlayersInfo + ' ' + playersCount + '/2', w / 2, h / 2);
    }
}

function loop() {
    requestAnimationFrame(loop);
    sendRequests();
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

function sendRequests() {
    if (gameStage === 'waiting-players' && requestsInterval === null) {
        requestsInterval = setInterval(() => {
            let clientsReq = formRequest('GET', 'data/clientsConnected', 'text');
            clientsReq.onload = () => { playersCount = +clientsReq.response };
            clientsReq.send();
        }, 5000)
    }
}