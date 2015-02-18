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
        'controllers.sideOrg',
        'controllers.sideCreate',
        'controllers.tasks',
        'http-auth-interceptor',
        'xeditable'
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
            templateUrl: 'views/tasks.html',
            controller: 'TasksController'
        })
            .state('tasks.list', {
                url: '/{listType:_my|_createdByMe|_myCandidate}',
                templateUrl: 'views/tasks.list.html',
                controller: 'TaskListController'
            });
        $stateProvider.state('processes', {
            url: '/processes',
            templateUrl: 'views/processes.html'
        });

        $urlRouterProvider.otherwise('/');

    }])
    .run(['editableOptions', function(editableOptions) {
        editableOptions.theme = 'bs3'; // bootstrap3 theme. Can be also 'bs2', 'default'
    }]);
