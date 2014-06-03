angular.module('security.login.services', ['env'])
    // a service dedicated to communicate with server with login/logout request
    .factory('loginService', ['$http', 'envConstants', function ($http, envConstants) {
        var service = {
            // Get the first reason for needing a login
            getLoginReason: function () {
                // TODO: a try reason
                return null;
            },

            currentUser: null,
            // Attempt to authenticate a user by the given email and password
            login: function (username, password) {
                var result = $http.post(envConstants.LOGIN_URL, {
                    'username': username,
                    'password': password
                }).then(function (response) {
                        service.currentUser = response.data.user;
                        return response.data;
                    });
                return result;
            },

            isAuthenticated: function () {
                return !!service.currentUser;
            },

            // Is the current user an adminstrator?
            isAdmin: function () {
                // TODO !!(service.currentUser && service.currentUser.admin)
                return !!(service.currentUser);
            },
            logout: function () {
                var result = $http.get(envConstants.LOGOUT_URL).then(function (response) {
                    service.currentUser = null;
                }, function(response) {
                    alert('Failed to logout.')
                });
                return result;
            }
        };
        return service;
    }]);
