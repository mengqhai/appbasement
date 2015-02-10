angular.module('services.security', ['env'])
    .factory('loginService', ['$http', 'envConstants', function($http, envConstants) {
        var login = function(userId, password) {
            var promise = $http.post(envConstants.REST_BASE+"/login", {
                userId: userId,
                password: password
            });
            return promise;
        };
        var logout = function() {
            var promise = $http.get(envConstants.REST_BASE+"/logout");
            return promise;
        };

        var service = {
            login: login,
            logout: logout
        };
        return service;
    }]);