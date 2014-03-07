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
            .when('/datepicker', {
                templateUrl: 'views/datepicker.html',
                controller:'DatepickerCtrl'
            })
            .when('/teamsList', {
                templateUrl:'views/teamsList.html'
            })
            .when('/fileUpload', {
                templateUrl: 'views/fileUpload.html'
            })
            .otherwise({
                redirectTo: '/'
            });
    });
