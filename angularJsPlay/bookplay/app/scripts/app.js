'use strict';

angular.module('bookplayApp', [
        'bookplayApp.controllers',
        'bookplayApp.directives',
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
            .when('/restGreeting', {
                templateUrl: 'views/httpGreeting.html',
                controller: 'RestGreetingCtrl'
            })
            .when('/restPromiseGreeting', {
                templateUrl: 'views/httpGreeting.html',
                controller:'RestPromiseGreetingCtrl'
            })
            .otherwise({
                redirectTo: '/'
            });
    });
