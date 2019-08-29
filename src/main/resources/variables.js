"use strict";

const canvasElement = document.getElementById("canvas");
const c = canvasElement.getContext("2d");

let w = document.body.clientWidth;
let h = document.body.clientHeight;

let frame = 0;
let playersCount = 0;

let requestsInterval = null;

let gameStage = '';
let gameStageChanged = false;
let gameStages = {
    waitingPlayers: 'waiting-players',
    settingGame: 'setting-game'
};

let bgGradientColors = new Pair(
    new ColorHEX('#3A1C71'),
    new ColorHEX('#9b794c')
);
let bgGradientNoise = new Simple1DNoise(h / 5 * 3, 0.05);
let bgGradientH = (h / 2) - (bgGradientNoise.amplitude / 2);
let bgGradient = null;

const waitingPlayersInfo = 'Waiting for players...';