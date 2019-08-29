"use strict";

const canvasElement = document.getElementById("canvas");
const c = canvasElement.getContext("2d");

let w = document.body.clientWidth;
let h = document.body.clientHeight;

let gameStage = 'waiting-players';

let bgGradientColors = new Pair(
    new ColorHEX('#3A1C71'),
    new ColorHEX('#9b794c')
);

let bgGradient = c.createLinearGradient(0, (h / 4), w, (h / 4 * 3));
bgGradient.addColorStop(0, bgGradientColors.first.color);
bgGradient.addColorStop(1, bgGradientColors.second.color);