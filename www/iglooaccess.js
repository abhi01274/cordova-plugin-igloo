var exec = require('cordova/exec');

exports.initialize = function(clientId, clientSecret, success, error) {
    exec(success, error, 'IglooAccessPlugin', 'initialize', [clientId, clientSecret]);
};

exports.unlock = function(lockId, success, error) {
    exec(success, error, 'IglooAccessPlugin', 'unlock', [lockId]);
};
