'use strict';

angular.module('bookplayApp', [
        'ngResource',
        'ngRoute'
    ])
    .config(function ($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: 'views/main.html',
                controller: 'MainCtrl'
            })
            .when('/httpGreeting', {
                templateUrl: 'views/httpGreeting.html',
                controller: 'HttpGreetingCtrl'
            })
            .otherwise({
                redirectTo: '/'
            });
    });
