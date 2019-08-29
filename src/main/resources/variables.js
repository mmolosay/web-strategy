"use strict";

const canvasElement = document.getElementById("canvas");
const c = canvasElement.getContext("2d");

let w = document.body.clientWidth;
let h = document.body.clientHeight;
let frame = 0;

let gameStage = 'waiting-players';

let bgGradientColors = new Pair(
    new ColorHEX('#3A1C71'),
    new ColorHEX('#9b794c')
);
let bgGradientNoise = new Simple1DNoise(500, 0.1);
let bgGradient = null;