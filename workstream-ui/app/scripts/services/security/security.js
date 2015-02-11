'use strict';

angular.module('services.security', ['env'])
    .factory('loginService', ['$http', 'envConstants', 'envVars', function ($http, envConstants, envVars) {
        var login = function (userId, password) {
            var promise = $http.post(envConstants.REST_BASE + "/login", {
                userId: userId,
                password: password
            }, {
                params: {
                    api_key: envVars.getApiKey()
                }
            });
            promise.then(function (response) {
                if (response.data.success) {
                    envVars.setApiKey(response.data.apiToken);
                    envVars.setCurrentUser(response.data.user);
                }
            });
            return promise;
        };
        var logout = function () {
            var promise = $http.get(envConstants.REST_BASE + "/logout", {
                params: {
                    api_key: envVars.getApiKey()
                }
            });
            promise.then(function (response) {
                envVars.setApiKey(null);
                envVars.setCurrentUser(null);
            });
            return promise;
        };

        var service = {
            login: login,
            logout: logout
        };
        return service;
    }]);