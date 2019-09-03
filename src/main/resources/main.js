"use strict";

function init() {
    canvasElement.width = w;
    canvasElement.height = h;

    window.onresize = resize;

    setGameStage(GAME_STAGES.WAITING_PLAYERS);
}

function resize() {
    w = document.body.clientWidth;
    h = document.body.clientHeight;

    canvasElement.width = w;
    canvasElement.height = h;

    bgGradientNoise = new Simple1DNoise((w / ratio) / 5 * 3, (w / ratio) / 5 * 3 / 11200);

    gameStageChanged = true;
}

function clear() {
    if (gameStage === GAME_STAGES.WAITING_PLAYERS) clearWithGradient()
}

function draw() {
    if (gameStage === 'waiting-players') {
        if (gameStageChanged) {
            c.textBaseline = 'middle';
            c.textAlign = 'center';
            c.font = '64px RobotoThin';
            gameStageChanged = false;
        }
        c.fillStyle = '#f0f8ff';
        c.fillText(INFO.WAITING_PLAYERS + ' ' + players + '/2', w / 2, h / 2);
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
    if (gameStage === GAME_STAGES.WAITING_PLAYERS && reqInterval === null) {

        reqInterval = setInterval(() => {
            let clientsReq = HTTP.formGET(url + '/data/playersCount');
            clientsReq.onload = () => { players = +clientsReq.response };
            clientsReq.send();

            HTTP.formPOST(url + '/connection', 'isConnected=true').send('isConnected=true')
        }, 1000);
    }
}