"use strict";

//==================== VECTORS ====================//

// Vector2D

class UnitVector2D {

    constructor(x, y, angle) {
        if (arguments.length === 0) {
            this.x = this.y = this._x = this._y = this.angleRadians = 0;
            return;
        }

        if (arguments.length === 1) {
            this.x = this.y = this._x = this._y = arguments[0] || 0;
            this.angleRadians = 0;
            return;
        }
        if (arguments.length === 2) {
            this.x = this._x = x || 0;
            this.y = this._y = y || 0;
            this.angleRadians = 0;
            return;
        }

        this.x = this._x = x || 0;
        this.y = this._y = y || 0;
        this.angleRadians = angle || 0;
    }

    rotateBy = (degrees) => {
        let angle = degreesToRadians(degrees);
        this.angleRadians += angle;
        this.x = this.x * Math.cos(angle) - this.y * Math.sin(angle);
        this.y = this.x * Math.sin(angle) + this.y * Math.cos(angle);
    };

    rotateTo = (degrees) => {
        let angle = degreesToRadians(degrees);
        this.angleRadians = angle;
        this.x = this._x * Math.cos(angle) - this._y * Math.sin(angle);
        this.y = this._x * Math.sin(angle) + this._y * Math.cos(angle);
    }
}

//==================== NOISE ====================//

class Simple1DNoise {

    constructor(amplitude, scale) {
        this.amplitude = amplitude;
        this.scale = scale;
        this.size = 256;
        this.r = [];
        for (let i = 0; i < this.size; i++) this.r[i] = Math.random();
    }

    getVal = (x) => {
        let scaledX = x * this.scale;
        let xFloor = Math.floor(scaledX);
        let t = scaledX - xFloor;
        let tRemapSmoothstep = t * t * ( 3 - 2 * t );

        let xMin = xFloor % this.size;
        let xMax = ( xMin + 1 ) % this.size;

        let y = lerp(this.r[xMin], this.r[xMax], tRemapSmoothstep);

        return y * this.amplitude;
    };
}

//==================== COMMON ====================//

class Color {

    constructor() {}

}

class ColorRGB extends Color {

    constructor(r, g, b) {
        super();
        if (arguments.length === 0) {
            this.r = this.g = this.b = 0;
            return;
        }
        if (arguments.length === 1) {
            this.r = this.g = this.b = arguments[0];
            return;
        }
        if (arguments.length === 3) {
            this.r = r;
            this.g = g;
            this.b = b;
        }
    }

    toStr = () => {
        return 'rgb(' + this.r + ', ' + this.g + ', ' + this.b + ')';
    };

    copy = () => {
      return new ColorRGB(this.r, this.g, this.b);
    };

    partToHEX = (part) => {
        if (typeof part === 'string') part = +part;
        let hex = (part).toString(16);
        return hex.length === 1 ? '0' + hex : hex;
    };

    toHEX = () => {
        return new ColorHEX(
            '#' +
            this.partToHEX(this.r) +
            this.partToHEX(this.g) +
            this.partToHEX(this.b)
        );
    };
}

class ColorHEX extends Color {

    constructor(color) {
        super();
        if (arguments.length === 0) {
            this.color = '#000000';
            return;
        }
        if (arguments.length === 1 && typeof color === 'string') {
            this.color = ((color.charAt(0) === '#') ? color : ('#' + color));
        }
    }

    toStr = () => {
        return this.color;
    };

    copy = () => {
        console.log(this.color);
        return new ColorHEX(this.color);
    };

    toRGB = () => {
        let result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(this.color);
        let rgb = new ColorRGB();
        rgb.r = parseInt(result[1], 16);
        rgb.g = parseInt(result[2], 16);
        rgb.b = parseInt(result[3], 16);
        return rgb;
    }
}

class Pair {

    constructor(first, second) {
        if (arguments.length === 0) {
            this.first = this.second = '';
            return;
        }
        if (arguments.length === 1) {
            this.first = this.second = arguments[0];
            return;
        }
        if (arguments.length === 2) {
            this.first = first;
            this.second = second;
        }
    }

    copy = () => {
        let pair = new Pair();
        pair.first = this.first.copy();
        pair.second = this.second.copy();
        return pair;
    };
}

//==================== METHODS ====================//

function intInRange(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function map(fromValue, fromBottomLimit, fromTopLimit, toBottomLimit, toTopLimit) {
    let initFract = (fromValue - fromBottomLimit) / (fromTopLimit - fromBottomLimit);
    let givenRange = toTopLimit - toBottomLimit;
    return initFract * givenRange + toBottomLimit;
}

function degreesToRadians(degrees) {
    if (degrees >= 360) degrees = degrees % 360;
    return degrees * Math.PI / 180;
}

function lerp(a, b, t) {
    return a * (1 - t) + b * t;
}

function getElapsedTimeMs(start) {
    return (new Date() - start);
}

//==================== HTTP ====================//

class HTTP {

    static formGETrequest = (url, method = 'GET', expectingResponseType = 'text') => {
        let xhr = new XMLHttpRequest();
        xhr.open(method, url, true);
        xhr.responseType = expectingResponseType;
        return xhr;
    };

    static getResponse = (url) => {
        let xhr = new XMLHttpRequest();
        xhr.open('GET', url, true);
        return xhr;
    };

    static postResponse = (url, data) => {
        let xhr = new XMLHttpRequest();
        xhr.open('POST', url, true);
        xhr.setRequestHeader('Content-type', 'text/plain');
        xhr.send(data);
    };

    static listen = (url) => {
        let es = new EventSource(url);
        es.onmessage = (message) => {
            console.log(message.data)
        };
        return es;
    };
}