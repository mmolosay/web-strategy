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

    bgGradientNoise = new Simple1DNoise((w / ratio) / 5 * 3, (w / ratio) / 7 * 3 / 13000);

    gameStageChanged = true;
}

function clear() {
    c.globalAlpha = 1;
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
        c.fillText(INFO.WAITING_PLAYERS  + players + '/2', w / 2, h / 2);
        return;
    }

    if (gameStage === GAME_STAGES.SETTING) {
        if (gameStageChanged) {
            c.strokeStyle = '#f0f8ff';
            c.lineWidth = 2;
            gameStageChanged = false;
        }
        drawSetting();
        return;
    }
}

function update() {
    if (players === PLAYERS_MAX && gameStage === GAME_STAGES.WAITING_PLAYERS) {
        setTimeout(setGameStage, 3000, GAME_STAGES.SETTING);
    }

    frame++;
}

function loop() {
    requestAnimationFrame(loop);
    clear();
    draw();
    update();
}

//==================================================//

function clearWithGradient() {
    let h1 = bgGradientStartY + bgGradientNoise.getVal(frame);
    let h2 = lerp(h1, h / 2, 1.3);

    bgGradientFill = c.createLinearGradient(0, h1, w, h2);
    bgGradientFill.addColorStop(0, bgGradientColors.first.color);
    bgGradientFill.addColorStop(1, bgGradientColors.second.color);

    c.fillStyle = bgGradientFill;
    c.fillRect(0, 0, w, h);
}

function setGameStage(stage) {
    gameStage = stage;
    gameStageChanged = true;
}

function drawSetting() {
    c.fillStyle = '#f0f8ff';
    if (host) {
        c.fillText('ROUNDS', w / 2, h / 2 - 50 - 32);
        c.beginPath();
        c.moveTo(w / 2 - 200, h / 2 - 46);
        c.lineTo(w / 2 + 200, h / 2 - 46);
        c.stroke();
        c.fillText(rounds.toString(), w / 2, h / 2 + 5);
        c.fillText("◄", w / 2 - 200 + 25 + 10, h / 2 + 5);
        c.fillText("►", w / 2 + 200 - 25 - 10, h / 2 + 5);
        c.fillText(!settingReady ? "READY" : "NOT READY", w / 2, h / 2 + 5 - 10 + 120);
        c.globalAlpha = 0.1;
        if (settingReady)
            c.fillStyle = readyColors.second;
        else
            c.fillStyle = readyColors.first;
        c.fillRect(w / 2 - 200, h / 2 + 70, 400, 80);
        c.strokeRect(w / 2 - 200, h / 2 + 70, 400, 80);
    }
    else {
        c.fillText(rounds + ' ROUND' + ((rounds > 1) ? 'S' : ''), w / 2, h / 2 - 50 - 32);
        c.beginPath();
        c.moveTo(w / 2 - 200, h / 2 - 46);
        c.lineTo(w / 2 + 200, h / 2 - 46);
        c.stroke();
        c.fillText(!settingReady ? "READY" : "NOT READY", w / 2, h / 2 + 5 - 10 + 120);
        c.globalAlpha = 0.1;
        if (settingReady)
            c.fillStyle = readyColors.second;
        else
            c.fillStyle = readyColors.first;
        c.fillRect(w / 2 - 200, h / 2 + 70, 400, 80);
        c.strokeRect(w / 2 - 200, h / 2 + 70, 400, 80);
    }
}

function sendRequests() {
    reqInterval = setInterval(() => {

        HTTP.formPOST(url + '/connection', 'isConnected=true').send('isConnected=true');

        if (gameStage === GAME_STAGES.WAITING_PLAYERS) {
            let req = HTTP.formGET(url + '/data/players');
            req.onload = () => { players = +req.response; };
            req.send();

            if (hostTimeout === null && host === null) {
                hostTimeout = setTimeout(() => {
                    let req = HTTP.formGET(url + '/data/isHost');
                    req.onload = () => { host = (req.response === 'true'); };
                    req.send();
                }, 1000);
            }
            return;
        }

        if (gameStage === GAME_STAGES.SETTING) {
            if (!host) {
                let req = HTTP.formGET(url + '/data/rounds');
                req.onload = () => { rounds = +req.response; };
                req.send();
            }
        }

    }, 1000);
}