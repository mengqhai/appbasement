'use strict';

angular.module('services.security', ['env'])
    .factory('loginService', ['$http', 'envConstants', 'envVars', function($http, envConstants, envVars) {
        var login = function(userId, password) {
            var config = {
                params: {
                    api_token: envVars.apiKey
                }
            };
            var promise = $http.post(envConstants.REST_BASE+"/login", {
                userId: userId,
                password: password
            }, config);
            promise.then(function(response) {
                if (response.data.success) {
                    envVars.apiKey = response.data.apiToken;
                }
            });
            return promise;
        };
        var logout = function() {
            var promise = $http.get(envConstants.REST_BASE+"/logout", config);
            return promise;
        };

        var service = {
            login: login,
            logout: logout
        };
        return service;
    }]);