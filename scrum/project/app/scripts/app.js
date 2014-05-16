'use strict';

angular
    .module('scrumApp', [
        'ngCookies',
        'ngResource',
        'ngSanitize',
        'ngRoute',
        'security.service'
    ])
    .config(function ($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: 'views/main.html',
                controller: 'MainCtrl'
            })
            .otherwise({
                redirectTo: '/'
            });
    });
