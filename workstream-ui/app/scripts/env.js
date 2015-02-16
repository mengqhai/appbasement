'use strict';

angular.module('env', ['LocalStorageModule'])
    .constant('URL_BASE', 'http://localhost:8080/workstream-rest')
    .factory('envConstants', ['URL_BASE', function (URL_BASE) {
        return {
            URL_BASE: URL_BASE,
            REST_BASE: URL_BASE + '/rest'
        };
    }])
    .factory('envVars', ['localStorageService', function (localStorageService) {
        var apiKey = localStorageService.get('apiKey');
        var currentUser = localStorageService.get('currentUser');
        var envVars = {
            setApiKey: function (newValue) {
                apiKey = newValue;
                localStorageService.set('apiKey', apiKey);
            },
            getApiKey: function () {
                return apiKey;
            },
            setCurrentUser: function (newUser) {
                currentUser = newUser;
                localStorageService.set('currentUser', newUser);
            },
            getCurrentUser: function () {
                return currentUser;
            },
            isLoggedIn: function () {
                return currentUser && apiKey;
            },
            getCurrentUserIdBase64: function () {
                return envVars.isLoggedIn() ? btoa(currentUser.id) : '';
            },
            getCurrentUserId: function() {
                return envVars.isLoggedIn() ? currentUser.id : null;
            }
        };
        return envVars;
    }])
    .run(['$rootScope', 'envVars', function ($rootScope, envVars) {
        $rootScope.isLoggedIn = envVars.isLoggedIn;
        $rootScope.getCurrentUser = envVars.getCurrentUser;
        $rootScope.getCurrentUserId = envVars.getCurrentUserId;
    }]);
