'use strict';

angular
    .module('scrumApp', [
        'ngCookies',
        'ngResource',
        'ngSanitize',
        'ngRoute',
        'security.service',
        'resources.projects',
        'resources.backlogs',
        'ui.bootstrap.pagination',
        'angular-loading-bar',
        'validateEquals', 'formPatchable', 'uniqueChecks', 'alert'
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
