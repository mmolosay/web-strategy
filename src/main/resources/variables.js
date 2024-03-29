"use strict";

const canvas = document.getElementById("canvas");
const c = canvas.getContext("2d");

let w = document.body.clientWidth;
let h = document.body.clientHeight;
let ratio = w / h;
const requestAnimationFrame = window.requestAnimationFrame
    || window.mozRequestAnimationFrame
    || window.webkitRequestAnimationFrame
    || window.msRequestAnimationFrame;

let frame = 0;
let players = 0;

let reqInterval = null;
let gameStageTimeout = null;
const url = 'http://167.99.246.51:8080';

let gameStage = null;
let gameStageChanged = false;

const bgGradientColors = new Pair(
    new ColorHEX('#667db6'),
    new ColorHEX('#0082c8'),
);
let bgGradientNoise = new Simple1DNoise(h / 5 * 3, h / 7 * 3 / 13000);
let bgGradientStartY = (h / 2) - (bgGradientNoise.amplitude / 2);
let bgGradientFill = null;

const GAME_STAGES = {
    WAITING_PLAYERS: 'waiting-players',
    SETTING: 'setting',
    GAME: 'game'
};
const INFO = {
    WAITING_CONNECTIONS: "Waiting for players… ",
    WAITING_READY: "Waiting players to be ready: "
};

const ROUNDS_MIN = 1;
const ROUNDS_MAX = 7;
const ROUNDS_DEFAULT = 3;

let players_max = 1;

let isHost = null;
let hostTimeout = null;
let rounds = ROUNDS_DEFAULT;
let round = 1;
let settingReady = false;
let playersReady = null;
const readyColor = '#73ff73';

const landscape = new Image();
const bushes = new Image();
const cloud1 = new Image();
const cloud2 = new Image();
const thisPlayer = new Image();
const thatPlayer = new Image();

const bottomOffset = (w > 1920 ? 90 : 0);
let pW = null;
let pH = null;

const CLOUDS = new Pair({
    pos: new Vector2D(null, null),
    speed: null,
    HEIGHT_MIN: 20,
    HEIGHT_MAX: 100,
    reset: () => {
        let speed = Math.random() + 0.5;
        while (speed === 0) { speed = Math.random() + 0.5; }
        CLOUDS.first.speed = speed;
        CLOUDS.first.pos.set(
            intInRange(-1000, -500),
            intInRange(CLOUDS.first.HEIGHT_MIN, CLOUDS.first.HEIGHT_MAX)
        )
    },
    move: () => { CLOUDS.first.pos.x += CLOUDS.first.speed; }
}, {
    pos: new Vector2D(null, null),
    speed: null,
    HEIGHT_MIN: 150,
    HEIGHT_MAX: 250,
    reset: () => {
        let speed = -Math.random() - 0.5;
        while (speed === 0) { speed = -Math.random() - 0.5; }
        CLOUDS.second.speed = speed;
        CLOUDS.second.pos.set(
            intInRange(w, w + 500),
            intInRange(CLOUDS.second.HEIGHT_MIN, CLOUDS.second.HEIGHT_MAX)
        )
    },
    move: () => { CLOUDS.second.pos.x += CLOUDS.second.speed; }
});

let thisDistLose = null;
let thatDistLose = null;
let uiCells = null;
let uiCellsLength = null;

const psDistBetween = 2;
const uiCellsOffset = 200;
const uiCellsMargin = 10;
let uiCellsHeight = h - 270 - bottomOffset;
const uiCellsPadding = 10;
const uiCellsLoseColor = '#d63d3d';
const uiCellsPlayerColor = '#4f4fff';
const uiCellsColor = '#f0f8ff';

let isTurn = null;

const roundsOnClickLess = (event) => {
    if (event.clientX >= (w / 2 - 200 + 25 + 10 - 30) &&
        event.clientX <= (w / 2 - 200 + 25 + 10 + 30) &&
        event.clientY >= (h / 2 + 5 - 32) &&
        event.clientY <= (h / 2 + 5 + 32))
    {
        if (rounds - 2 > ROUNDS_MIN - 1 && !settingReady) rounds -= 2;
    }
};
const roundsOnClickMore = (event) => {
    if (event.clientX >= (w / 2 + 200 - 25 - 10 - 30) &&
        event.clientX <= (w / 2 + 200 - 25 - 10 + 30) &&
        event.clientY >= (h / 2 + 5 - 32) &&
        event.clientY <= (h / 2 + 5 + 32))
    {
        if (rounds + 2 < ROUNDS_MAX + 1 && !settingReady) rounds += 2;
    }
};
const settingOnClickReady = (event) => {
    if (event.clientX >= (w / 2 - 200) &&
        event.clientX <= (w / 2 + 200) &&
        event.clientY >= (h / 2 + 100) &&
        event.clientY <= (h / 2 + 180))
    {
        settingReady = !settingReady;
        if (settingReady) {
            if (isHost) HTTP.formPOST(url, '/data').send('rounds=' + rounds);
            HTTP.formPOST(url, '/data').send('isReady=true');
        }
        else {
            HTTP.formPOST(url, '/data').send('isReady=false');
        }
    }
};
const onMove = (event)  => {

    if (gameStage === GAME_STAGES.GAME && isTurn) {
        if (event.code === 'ArrowLeft' || event.code === 'ArrowRight') {
            if (event.code === 'ArrowLeft') {
                thisDistLose--;
                thatDistLose++;
            }
            else {
                thisDistLose++;
                thatDistLose--;
            }
            HTTP.formPOST(url + '/data').send('thisDistLose=' + thisDistLose);
            HTTP.formPOST(url + '/data').send('thatDistLose=' + thatDistLose);
            HTTP.formPOST(url + '/data').send('turnReverse');
            isTurn = false;
        }
    }
};