'use strict';

angular
    .module('scrumApp', [
        'ngCookies',
        'ngResource',
        'ngSanitize',
        'ngRoute',
        'ngAnimate',
        'ngLocale',
        'controllers',
        'security.service',
        'resources.projects',
        'resources.backlogs',
        'ui.bootstrap.pagination',
        'angular-loading-bar',
        'validateEquals', 'formPatchable', 'uniqueChecks', 'alert', 'field'
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
