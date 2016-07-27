/*global cordova, module*/

module.exports = {
    greet: function (name, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "Draw", "greet", [name]);
    },
    draw: function(name,successCallback,errorCallback ){
        cordova.exec(successCallback, errorCallback, "Draw", "draw", [name]);
    }
};
