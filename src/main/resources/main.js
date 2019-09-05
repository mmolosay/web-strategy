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

    c.font = '64px RobotoThin';
    c.textBaseline = 'middle';
    c.textAlign = 'center';

    gameStageChanged = true;
}

function clear() {
    let h1 = bgGradientStartY + bgGradientNoise.getVal(frame);
    let h2 = lerp(h1, h / 2, 1.3);

    bgGradientFill = c.createLinearGradient(0, h1, w, h2);
    bgGradientFill.addColorStop(0, bgGradientColors.first.color);
    bgGradientFill.addColorStop(0.25, bgGradientColors.second.color);
    bgGradientFill.addColorStop(0.75, bgGradientColors.second.color);
    bgGradientFill.addColorStop(1, bgGradientColors.first.color);

    c.fillStyle = bgGradientFill;
    c.fillRect(0, 0, w, h);
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
        c.fillText(INFO.WAITING_CONNECTIONS  + players + '/2', w / 2, h / 2);
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

    if (gameStage === GAME_STAGES.GAME) {
        if (gameStageChanged) {
            //
            gameStageChanged = false;
        }
        drawGame();
    }
}

function update() {
    frame++;

    if (gameStageTimeout === null) {
        if (players === 1 && gameStage === GAME_STAGES.WAITING_PLAYERS) {
            gameStageTimeout = setTimeout(setGameStage, 3000, GAME_STAGES.SETTING);
            return;
        }
        if (players === playersReady && gameStage === GAME_STAGES.SETTING) {
            gameStageTimeout = setTimeout(setGameStage, 3000, GAME_STAGES.GAME);
        }
    }
}

function loop() {
    requestAnimationFrame(loop);
    clear();
    draw();
    update();
}

//==================================================//

function setGameStage(stage) {
    gameStage = stage;
    gameStageTimeout = null;
    gameStageChanged = true;
}

function drawSetting() {
    c.fillStyle = '#f0f8ff';
    c.font = '64px RobotoThin';
    if (host) {
        c.fillText('ROUNDS', w / 2, h / 2 - 50 - 32);
        c.beginPath();
        c.moveTo(w / 2 - 200, h / 2 - 46);
        c.lineTo(w / 2 + 200, h / 2 - 46);
        c.stroke();
        c.fillText(rounds.toString(), w / 2, h / 2 + 5);
        c.fillText("◄", w / 2 - 200 + 25 + 10, h / 2 + 5);
        c.fillText("►", w / 2 + 200 - 25 - 10, h / 2 + 5);
    }
    else {
        c.fillText(rounds + ' ROUND' + ((rounds > 1) ? 'S' : ''), w / 2, h / 2 - 50 - 32);
        c.beginPath();
        c.moveTo(w / 2 - 200, h / 2 - 46);
        c.lineTo(w / 2 + 200, h / 2 - 46);
        c.stroke();
    }
    drawReadyButton();
    if (settingReady) {
        c.font = '22px RobotoThin';
        c.globalAlpha = 0.8;
        c.fillText(
            INFO.WAITING_READY + playersReady + '/' + players + ((players === playersReady) ? '. Starting…' : '.'),
            w / 2,
            h / 2 + 80
        );
        c.globalAlpha = 1;
    }
}

function drawReadyButton() {
    c.fillText(!settingReady ? "READY" : "NOT READY", w / 2, h / 2 + 5 - 10 + 150);
    c.globalAlpha = 0.2;
    if (settingReady) {
        c.fillStyle = readyColor;
        c.fillRect(w / 2 - 200, h / 2 + 100, 400, 80);
        c.fillStyle = '#f0f8ff';
    }
    c.globalAlpha = 0.5;
    c.strokeRect(w / 2 - 200, h / 2 + 100, 400, 80);
    c.globalAlpha = 1;
}

function drawGame() {

}

function sendRequests() {
    reqInterval = setInterval(() => {

        HTTP.formPOST(url + '/connection').send('isConnected=true');

        if (gameStage === GAME_STAGES.WAITING_PLAYERS) {
            let req = HTTP.formGET(url + '/data/players');
            req.onload = () => { players = +req.response; };
            req.send();

            if (hostTimeout === null && host === null) {
                hostTimeout = setTimeout(() => {
                    let req = HTTP.formGET(url + '/data/isHost');
                    req.onload = () => { host = (req.response === 'true'); };
                    // req.onload = () => { host = false; };
                    req.send();

                    HTTP.formPOST(url + '/data').send('isReady=false');
                }, 1000);
            }
            return;
        }

        if (gameStage === GAME_STAGES.SETTING) {
            let req = HTTP.formGET(url + '/data/playersReady');
            req.onload = () => { playersReady = +req.response; };
            req.send();

            if (!host) {
                let req = HTTP.formGET(url + '/data/rounds');
                req.onload = () => { rounds = +req.response; };
                req.send();
            }
            return;
        }

    }, 1000);
}