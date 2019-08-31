"use strict";

init();
loop();

// setTimeout(() => {
//     HTTP.getResponse(url + '/connection')
// }, 5000);

setTimeout(() => {
    HTTP.postResponse(url, "isConnected=true");
}, 5500);