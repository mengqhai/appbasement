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
        return {
            setApiKey: function (newValue) {
                apiKey = newValue;
                localStorageService.set('apiKey', apiKey);
            },
            getApiKey: function() {
                return apiKey;
            },
            setCurrentUser: function(newUser) {
                currentUser = newUser;
                localStorageService.set('currentUser', newUser);
            },
            getCurrentUser: function() {
                return currentUser;
            }
        };
    }]);
