'use strict';

angular
    .module('scrumApp', [
        'security.service',
        'ngCookies',
        'ngResource',
        'ngSanitize',
        'ui.router',//'ngRoute',
        'ngAnimate',
        'ngLocale',
        'controllers',
        'services',
        'resources.projects',
        'resources.backlogs',
        'ui.bootstrap.pagination',
        'angular-loading-bar',
        'validateEquals', 'formPatchable', 'uniqueChecks', 'alert', 'field',
        'filters'
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
