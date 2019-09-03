"use strict";

const canvas = document.getElementById("canvas");
const c = canvas.getContext("2d");

let w = document.body.clientWidth;
let h = document.body.clientHeight;
let ratio = w / h;

let frame = 0;
let players = 0;

let reqInterval = null;
let url = 'http://167.99.246.51:8080';

let gameStage = '';
let gameStageChanged = false;

let bgGradientColors = new Pair(
    new ColorHEX('#3A1C71'),
    new ColorHEX('#9b794c')
);
let bgGradientNoise = new Simple1DNoise(h / 7 * 3, h / 7 * 3 / 13000);
let bgGradientH = (h / 2) - (bgGradientNoise.amplitude / 2);
let bgGradient = null;

const GAME_STAGES = {
    WAITING_PLAYERS: 'waiting-players',
    SETTING: 'setting'
};
const INFO = {
    WAITING_PLAYERS: "Waiting for players…"
};

const ROUNDS_MIN = 1;
const ROUNDS_MAX = 7;
const ROUNDS_DEFAULT = 3;

let rounds = ROUNDS_DEFAULT;
let round = 1;
let settingReady = false;
let readyColors = new Pair('#ff7373', '#73ff73');

const roundsOnClickLess = (event) => {
    if (event.clientX >= (w / 2 - 200 + 25 + 10 - 30) &&
        event.clientX <= (w / 2 - 200 + 25 + 10 + 30) &&
        event.clientY >= (h / 2 + 5 - 32) &&
        event.clientY <= (h / 2 + 5 + 32))
    {
        if (rounds - 2 > ROUNDS_MIN - 1) rounds -= 2;
    }
};
const roundsOnClickMore = (event) => {
    if (event.clientX >= (w / 2 + 200 - 25 - 10 - 30) &&
        event.clientX <= (w / 2 + 200 - 25 - 10 + 30) &&
        event.clientY >= (h / 2 + 5 - 32) &&
        event.clientY <= (h / 2 + 5 + 32))
    {
        if (rounds + 2 < ROUNDS_MAX + 1) rounds += 2;
    }
};
const settingOnClickReady = (event) => {
    if (event.clientX >= (w / 2 - 200) &&
        event.clientX <= (w / 2 + 200) &&
        event.clientY >= (h / 2 + 70) &&
        event.clientY <= (h / 2 + 150))
    {
        settingReady = !settingReady;
    }
};