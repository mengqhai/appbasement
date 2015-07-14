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
        'directives.datepicker',
        'directives.filemodel',
        'directives.showmore',
        'directives.statusToggle',
        'directives.popoverToggle',
        'resources.users',
        'resources.orgs',
        'resources.groups',
        'resources.models',
        'controllers.currentUser',
        'controllers.sideOrg',
        'controllers.sideCreate',
        'controllers.tasks',
        'controllers.projects',
        'controllers.models',
        'controllers.templates',
        'controllers.processes',
        'controllers.account',
        'controllers.orgSettings',
        'controllers.orgs',
        'controllers.groups',
        'controllers.dashboard',
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
            url: '/tasks?{status:active|archived}',
            templateUrl: 'views/tasks.html',
            controller: 'TasksController'
        })
            .state('tasks.list', {
                url: '/{listType:_my|_createdByMe|_myCandidate}/',
                templateUrl: 'views/tasks.list.html',
                controller: 'TaskListController'
            });
        $stateProvider.state('processes', {
            url: '/processes?{status:active|archived}',
            templateUrl: 'views/processes.html',
            controller: 'ProcessListController'
        });
        $stateProvider.state('processes.details', {
            url: '/{processId}',
            templateUrl: 'views/processes.details.html',
            controller: 'ProcessDetailsController'
        });
        $stateProvider.state('project', {
            url: '/project/{projectId}',
            templateUrl: 'views/project.html',
            controller: 'ProjectController'
        });
        $stateProvider.state('project.tasks', {
            url: '/tasks?{status:active|archived}',
            templateUrl: 'views/project.tasks.html',
            controller: 'ProjectTaskListController'
        });
        $stateProvider.state('project.members', {
            url: '/members',
            templateUrl: 'views/project.members.html',
            controller: 'ProjectMemberController'
        })
        $stateProvider.state('models', {
            url: '/orgs/{orgId}/models',
            templateUrl: 'views/models.html',
            controller: 'ModelListController',
            resolve: {
                models: ['Orgs', '$stateParams', function (Orgs, $stateParams) {
                    return Orgs.getModelsInOrg({orgId: $stateParams.orgId});
                }]}
        })
        $stateProvider.state('models.details', {
            url: '/{modelId}',
            templateUrl: 'views/models.details.html',
            controller: 'ModelDetailsController'
        })
        $stateProvider.state('templates', {
            url: '/orgs/{orgId}/templates',
            templateUrl: 'views/templates.html',
            controller: 'TemplateListController',
            resolve: {
                templates: ['Orgs', '$stateParams', function (Orgs, $stateParams) {
                    return Orgs.getTemplatesInOrg({orgId: $stateParams.orgId});
                }]
            }
        })
        $stateProvider.state('templates.details', {
            url: '/{templateId}',
            templateUrl: 'views/templates.details.html',
            controller: 'TemplateDetailsController'
        });
        $stateProvider.state('account', {
            url: '/account',
            templateUrl: 'views/account.html'
        });
        $stateProvider.state('account.settings', {
            url: '/settings',
            templateUrl: 'views/account.settings.html',
            controller: 'AccountSettingsController'
        });
        $stateProvider.state('account.info', {
            url: '/info',
            templateUrl: 'views/account.info.html',
            controller: 'AccountInfoController'
        });
        $stateProvider.state('account.pic', {
            url: '/pic',
            templateUrl: 'views/account.pic.html',
            controller: 'AccountPicController'
        });
        $stateProvider.state('account.password', {
            url: '/password',
            templateUrl: 'views/account.password.html',
            controller: 'AccountPasswordController'
        });
        $stateProvider.state('orgSettings', {
            url: '/orgSettings',
            templateUrl: 'views/orgSettings.html',
            controller: 'OrgSettingsController'
        });
        $stateProvider.state('orgSettings.general', {
            url: '/orgSettings/{orgId}/general',
            templateUrl: 'views/orgSettings.general.html',
            controller: 'OrgSettingsGeneralController'
        });
        $stateProvider.state('orgSettings.projects', {
            url: '/orgSettings/{orgId}/projects',
            templateUrl: 'views/orgSettings.projects.html',
            controller: 'OrgSettingsProjectsController'
        });
        $stateProvider.state('orgSettings.members', {
            url: '/orgSettings/{orgId}/members',
            templateUrl: 'views/orgSettings.members.html',
            controller: 'OrgSettingsMembersController'
        });
        $stateProvider.state('dashboard', {
            url: '/dashboard',
            templateUrl: '/views/dashboard.html',
            controller: 'DashboardController'
        });
//        $stateProvider.state('dashboard.overview', {
//            url: '/overview',
//            templateUrl: '/views/dashboard.overview.html',
//            controller: 'DashboardOverviewController'
//        })
        $stateProvider.state('dashboard.notification', {
            url: '/notifications/{notificationId}',
            // abstract: true,
            // Note: abstract still needs a ui-view for its children to populate.
            // You can simply add it inline here.
            templateUrl: 'views/dashboard.notifications.html'
        })
        $stateProvider.state('dashboard.notification.process', {
            url: '/process/{processId}',
            templateUrl: '/views/dashboard.notifications.process.html'
        });
        $stateProvider.state('dashboard.notification.task', {
            url: '/task/{taskId}',
            templateUrl: '/views/dashboard.notifications.task.html',
            controller: 'DashboardTaskController'
        })

        $urlRouterProvider.otherwise('/');

    }])
    .config(function ($tooltipProvider) {
        $tooltipProvider.setTriggers({'open': 'close'});
    })
    .run(['editableOptions', function (editableOptions) {
        editableOptions.theme = 'bs3'; // bootstrap3 theme. Can be also 'bs2', 'default'
    }]);
