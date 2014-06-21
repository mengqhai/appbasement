'use strict';

angular
    .module('scrumApp', [
        'ngCookies',
        'ngResource',
        'ngSanitize',
        'ui.router',//'ngRoute',
        'ngAnimate',
        'ngLocale',
        'controllers',
        'services',
        'security.service',
        'resources.projects',
        'resources.backlogs',
        'ui.bootstrap.pagination',
        'angular-loading-bar',
        'validateEquals', 'formPatchable', 'uniqueChecks', 'alert', 'field'
    ])
    .config(function ($stateProvider,$urlRouterProvider, $httpProvider) {
        $stateProvider
            .state('home', {
                url: '/',
                templateUrl: 'views/main.html',
                controller: 'MainCtrl',
                data: {
                    title: 'Home'
                }
            });
        $urlRouterProvider.otherwise('/');
        $httpProvider.defaults.withCredentials=true;
    });
