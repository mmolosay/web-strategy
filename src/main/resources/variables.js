"use strict";

const canvasElement = document.getElementById("canvas");
const c = canvasElement.getContext("2d");

let w = document.body.clientWidth;
let h = document.body.clientHeight;
let ratio = w / h;

let frame = 0;
let playersCount = 0;

let requestsInterval = null;
let url = 'http://167.99.246.51:8080/';

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
let bgGradientNoise = new Simple1DNoise(h / 5 * 3, h / 5 * 3 / 11200);
let bgGradientH = (h / 2) - (bgGradientNoise.amplitude / 2);
let bgGradient = null;

const waitingPlayersInfo = 'Waiting for players...';