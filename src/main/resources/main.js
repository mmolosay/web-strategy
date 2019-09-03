"use strict";

function init() {
    canvas.width = w;
    canvas.height = h;

    window.onresize = resize;
    window.addEventListener('click', roundsOnClickLess);
    window.addEventListener('click', roundsOnClickMore);
    window.addEventListener('click', settingOnClickReady);

    setGameStage(GAME_STAGES.WAITING_PLAYERS);
}

function resize() {
    w = document.body.clientWidth;
    h = document.body.clientHeight;

    canvas.width = w;
    canvas.height = h;

    bgGradientNoise = new Simple1DNoise((w / ratio) / 7 * 3, (w / ratio) / 7 * 3 / 13000);

    gameStageChanged = true;
}

function clear() {
    if (gameStage === GAME_STAGES.WAITING_PLAYERS) clearWithGradient();
    if (gameStage === GAME_STAGES.SETTING) clearWithGradient();
}

function draw() {
    if (gameStage === GAME_STAGES.WAITING_PLAYERS) {
        if (gameStageChanged) {
            c.textBaseline = 'middle';
            c.textAlign = 'center';
            c.font = '64px RobotoThin';
            gameStageChanged = false;
        }
        c.fillStyle = '#f0f8ff';
        c.fillText(INFO.WAITING_PLAYERS + ' ' + players + '/2', w / 2, h / 2);
    }

    if (gameStage === GAME_STAGES.SETTING) {
        if (gameStageChanged) {
            c.strokeStyle = '#f0f8ff';
            c.lineWidth = 2;
            gameStageChanged = false;
        }
        c.fillStyle = '#f0f8ff';
        c.fillText('ROUNDS', w / 2, h / 2 - 50 - 32);
        c.beginPath();
        c.moveTo(w / 2 - 200, h / 2 - 46);
        c.lineTo(w / 2 + 200, h / 2 - 46);
        c.stroke();
        c.fillText(rounds.toString(), w / 2, h / 2 + 5);
        c.fillText("◄", w / 2 - 200 + 25 + 10, h / 2 + 5);
        c.fillText("►", w / 2 + 200 - 25 - 10, h / 2 + 5);
        c.fillText(!settingReady ? "READY" : "NOT READY", w / 2, h / 2 + 5 - 10 + 120);
        c.globalAlpha = 0.2;
        if (settingReady)
            c.fillStyle = readyColors.second;
        else
            c.fillStyle = readyColors.first;
        c.fillRect(w / 2 - 200, h / 2 + 70, 400, 80);
        c.globalAlpha = 1;
        c.strokeRect(w / 2 - 200, h / 2 + 70, 400, 80);
    }
}

function loop() {
    requestAnimationFrame(loop);
    sendRequests();
    clear();
    draw();
    frame++;
    if (players === 1) setTimeout(setGameStage, 3000, GAME_STAGES.SETTING);
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
    if (gameStage === GAME_STAGES.WAITING_PLAYERS && reqInterval === null)

        reqInterval = setInterval(() => {
            let clientsReq = HTTP.formGET(url + '/data/playersCount');
            clientsReq.onload = () => { players = +clientsReq.response };
            clientsReq.send();

            HTTP.formPOST(url + '/connection', 'isConnected=true').send('isConnected=true');

        }, 1000);
}