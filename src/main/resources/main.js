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

    bgGradientNoise = new Simple1DNoise((w / ratio) / 5 * 3, (w / ratio) / 5 * 3 / 11200);

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
            c.font = '64px RobotoThin';
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
    if (gameStage === 'waiting-players' && requestsInterval === null && responsesInterval === null) {

        // requestsInterval = setInterval(() => {
        //     let clientsReq = HTTP.formGETrequest(url + '/data/playersCount');
        //     clientsReq.onload = () => { playersCount = +clientsReq.response };
        //     clientsReq.send();
        // }, 5000);

        // responsesInterval = setInterval(() => {
        //     HTTP.postResponse(url + '/connection', "/connection?isConnected=true");
        // }, 5000)
    }
}