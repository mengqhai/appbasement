angular.module('security.login.services', ['env'])
    // a service dedicated to communicate with server with login/logout request
    .factory('loginService', ['$http', 'envConstants', '$cookieStore', function ($http, envConstants, $cookieStore) {
        var service = {
            currentUser: $cookieStore.get('currentUser'),
            // Attempt to authenticate a user by the given email and password
            login: function (username, password) {
                var result = $http.post(envConstants.LOGIN_URL, {
                    'username': username,
                    'password': password
                }).then(function (response) {
                        service.currentUser = response.data.user;
                        $cookieStore.put('currentUser', response.data.user);
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
                    $cookieStore.remove('currentUser');
                }, function(response) {
                    alert('Failed to logout.')
                });
                return result;
            }
        };
        return service;
    }]);
