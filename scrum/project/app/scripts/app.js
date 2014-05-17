'use strict';

angular
    .module('scrumApp', [
        'ngCookies',
        'ngResource',
        'ngSanitize',
        'ngRoute',
        'security.service'
    ])
    .config(function ($routeProvider, $httpProvider) {
        $routeProvider
            .when('/', {
                templateUrl: 'views/main.html',
                controller: 'MainCtrl'
            })
            .otherwise({
                redirectTo: '/'
            });
        $httpProvider.defaults.withCredentials=true;
    });
