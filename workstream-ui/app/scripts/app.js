'use strict';

/**
 * @ngdoc overview
 * @name workstreamUiApp
 * @description
 * # workstreamUiApp
 *
 * Main module of the application.
 */
angular
    .module('workstreamUiApp', [
        'ngAnimate',
        'ngCookies',
        'ngResource',
        'ngRoute',
        'ngSanitize',
        'ngTouch',
        'ui.sortable',
        'ui.router',
        'ui.bootstrap',
        'LocalStorageModule',
        'controllers.login',
        'directives.sidebar',
        'resources.users',
        'controllers.currentUser',
        'controllers.sideOrg'
    ])
    .config(['localStorageServiceProvider', function (localStorageServiceProvider) {
        localStorageServiceProvider.setPrefix('ls');
    }])
//    .config(function ($routeProvider) {
//        $routeProvider
//            .when('/', {
//                templateUrl: 'views/main.html',
//                controller: 'MainCtrl'
//            })
//            .when('/about', {
//                templateUrl: 'views/about.html',
//                controller: 'AboutCtrl'
//            })
//            .otherwise({
//                redirectTo: '/'
//            });
//    })
    .config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
        $stateProvider.state('main', {
            url: '/',
            templateUrl: 'views/main.html',
            controller: 'MainCtrl'
        });
        $stateProvider.state('about', {
            url: '/about',
            templateUrl: 'views/about.html',
            controller: 'AboutCtrl'
        });
        $stateProvider.state('tasks', {
            url: '/tasks',
            templateUrl: 'views/tasks.html'
        });
        $stateProvider.state('processes', {
            url: '/processes',
            templateUrl: 'views/processes.html'
        });

        $urlRouterProvider.otherwise('/');

    }]);
