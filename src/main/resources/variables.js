"use strict";

const canvas = document.getElementById("canvas");
const c = canvas.getContext("2d");

let w = document.body.clientWidth;
let h = document.body.clientHeight;
let ratio = w / h;

let frame = 0;
let players = 0;

let reqInterval = null;
let gameStageTimeout = null;
let url = 'http://167.99.246.51:8080';

let gameStage = '';
let gameStageChanged = false;

let bgGradientColors = new Pair(
    new ColorHEX('#667db6'),
    new ColorHEX('#427db7'),
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

const PLAYERS_MAX = 2;

let host = null;
let hostTimeout = null;
let rounds = ROUNDS_DEFAULT;
let round = 1;
let settingReady = false;
let playersReady = null;
let readyColor = '#73ff73';

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
            HTTP.formPOST(url, '/data').send('rounds=' + rounds);
            HTTP.formPOST(url, '/data').send('isReady=true');
        }
        else {
            HTTP.formPOST(url, '/data').send('isReady=false');
        }
    }
};