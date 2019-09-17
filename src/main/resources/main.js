"use strict";

function init() {
    canvas.width = w;
    canvas.height = h;

    window.onresize = resize;
    window.addEventListener('click', roundsOnClickLess);
    window.addEventListener('click', roundsOnClickMore);
    window.addEventListener('click', settingOnClickReady);
    window.addEventListener('keyup', onMove);

    c.imageSmoothingEnabled = true;
    c.imageSmoothingQuality = 'high';

    let req = HTTP.formGET(url + '/data/playersMax');
    req.onload = () => { PLAYERS_MAX = +req.response; };
    req.send();

    landscape.src = url + '/landscape.png';
    bushes.src = url + ((w > 1920) ? '/bushes-large.png' : '/bushes-common.png');
    cloud1.src = url + '/cloud1.png';
    cloud2.src = url + '/cloud2.png';
    p1.src = url + '/p1.png';
    p2.src = url + '/p2.png';
    p1.onload = () => {
        pW = uiCellsLength + uiCellsMargin * 2;
        pH = p1.height * pW / p1.width;
    };
    landscape.onload = () => {
        console.log(landscape.height)
    };

    CLOUDS.first.reset();
    CLOUDS.second.reset();

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

    uiCellsHeight = h - 270 - bottomOffset;

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
        c.fillText(INFO.WAITING_CONNECTIONS  + players + '/' + PLAYERS_MAX, w / 2, h / 2);
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
        }
        drawGame();
    }
}

function update() {
    frame++;

    if (gameStageTimeout === null) {
        if (players === PLAYERS_MAX && gameStage === GAME_STAGES.WAITING_PLAYERS) {
            gameStageTimeout = setTimeout(setGameStage, 1000, GAME_STAGES.SETTING);
            return;
        }
        if (players === playersReady && gameStage === GAME_STAGES.SETTING) {
            gameStageTimeout = setTimeout(setGameStage, 1000, GAME_STAGES.GAME);
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
    if (isHost) {
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
    drawGameAmbient();
    drawUI();
    let dH = bushes.height * w / bushes.width;
    c.drawImage(bushes, 0, 0, bushes.width, bushes.height, 0, h - dH, w, dH);
}

function drawGameAmbient() {
    drawClouds();
    c.drawImage(landscape, 0, h - landscape.height - bottomOffset);
}

function drawClouds() {
    c.drawImage(cloud1, CLOUDS.first.pos.x, CLOUDS.first.pos.y);
    c.drawImage(cloud2, CLOUDS.second.pos.x, CLOUDS.second.pos.y);

    CLOUDS.first.move();
    CLOUDS.second.move();

    if (CLOUDS.first.pos.x >= w) CLOUDS.first.reset();
    if (CLOUDS.second.pos.x <= -cloud1.width) CLOUDS.second.reset();
}

function drawUI() {
    drawCells();
    drawPlayers();
}

function drawCells() {
    let x = uiCellsOffset;
    let p1 = p1DistLose;
    let p2 = p1 + psDistBetween + 1;

    c.strokeStyle = uiCellsColor;
    c.lineWidth = 2;

    for (let i = 0; i < uiCells; i++) {
        x += uiCellsMargin;
        if (i === p1 || i === p2) {
            c.globalAlpha = 0.7;
            c.strokeStyle = uiCellsPlayerColor;
        }
        if (i === 0 || i === uiCells - 1) {
            c.globalAlpha = 0.7;
            c.strokeStyle = uiCellsLoseColor;
        }

        c.beginPath();
        c.moveTo(x, uiCellsHeight);
        x += uiCellsLength;
        c.lineTo(x, uiCellsHeight);
        c.stroke();

        if (i === p1 || i === p2 || i === 0 || i === uiCells - 1) {
            c.globalAlpha = 0.4;
            c.strokeStyle = uiCellsColor;
        }
        x += uiCellsMargin;
    }
    c.globalAlpha = 1;
}

function drawPlayers() {
    c.drawImage(p1, 0, 0, p1.width, p1.height,
        uiCellsOffset + (p1DistLose * (uiCellsLength + uiCellsMargin * 2)),
        uiCellsHeight - uiCellsPadding - pH,
        pW, pH);
    c.drawImage(p2, 0, 0, p2.width, p2.height,
        uiCellsOffset + ((p1DistLose + psDistBetween + 1) * (uiCellsLength + uiCellsMargin * 2)),
        uiCellsHeight - uiCellsPadding - pH,
        pW, pH);
}

function intervalRequests() {
    reqInterval = setInterval(() => {

        HTTP.formPOST(url + '/connection').send('isConnected=true');

        if (gameStage === GAME_STAGES.WAITING_PLAYERS) {
            let req = HTTP.formGET(url + '/data/players');
            req.onload = () => { players = +req.response; };
            req.send();

            if (hostTimeout === null && isHost === null) {
                hostTimeout = setTimeout(() => {
                    let req = HTTP.formGET(url + '/data/isHost');
                    req.onload = () => { isHost = (req.response === 'true'); };
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

            if (!isHost) {
                let req = HTTP.formGET(url + '/data/rounds');
                req.onload = () => { rounds = +req.response; };
                req.send();
            }
            return;
        }

        if (gameStage === GAME_STAGES.GAME) {
        }

    }, 1000);
}