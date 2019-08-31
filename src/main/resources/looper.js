"use strict";

init();
loop();

let listener;

    listener = HTTP.listen(url + '/event/subscribe');
    listener.onmessage = function (event) {
        console.log(event.data);
    };

// setTimeout(() => {
//     HTTP.getResponse(url + '/connection')
// }, 5000);

// setTimeout(() => {
//     let a = HTTP.getResponse(url + '/connection');
//     a.onreadystatechange = () => {
//         console.log("state")
//     };
//     a.send(null)
// }, 1000);

// setTimeout(() => {
//     HTTP.
// }, 5000);