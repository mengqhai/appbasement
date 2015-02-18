'use strict';

angular.module('env', ['LocalStorageModule'])
    .constant('URL_BASE', 'http://localhost:8080/workstream-rest')
    .factory('envConstants', ['URL_BASE', function (URL_BASE) {
        return {
            URL_BASE: URL_BASE,
            REST_BASE: URL_BASE + '/rest'
        };
    }])
    .factory('envVars', ['localStorageService', function (localStorageService) {
        var apiKey = localStorageService.get('apiKey');
        var currentUser = localStorageService.get('currentUser');
        var envVars = {
            setApiKey: function (newValue) {
                apiKey = newValue;
                localStorageService.set('apiKey', apiKey);
            },
            getApiKey: function () {
                return apiKey;
            },
            setCurrentUser: function (newUser) {
                currentUser = newUser;
                localStorageService.set('currentUser', newUser);
            },
            getCurrentUser: function () {
                return currentUser;
            },
            isLoggedIn: function () {
                return currentUser && apiKey;
            },
            getCurrentUserIdBase64: function () {
                return envVars.isLoggedIn() ? btoa(currentUser.id) : '';
            },
            getCurrentUserId: function () {
                return envVars.isLoggedIn() ? currentUser.id : null;
            }
        };
        return envVars;
    }])
    .run(['$rootScope', 'envVars', 'Orgs', function ($rootScope, envVars, Orgs) {
        $rootScope.isLoggedIn = envVars.isLoggedIn;
        $rootScope.getCurrentUser = envVars.getCurrentUser;
        $rootScope.getCurrentUserId = envVars.getCurrentUserId;


        $rootScope.myOrgs = [];
        $rootScope.myOrgMap = {};
        $rootScope.myProjects = [];
        $rootScope.myProjectMap = {};
        $rootScope.orgProjects = {};
        $rootScope.orgUsers = {};



        $rootScope.loadMyOrgs = function () {
            if (!envVars.isLoggedIn()) {
                return;
            }
            var orgs = Orgs.getMyOrgs();
            $rootScope.myOrgs = orgs;
            orgs.$promise.then(function() {
                for (var i=0;i<orgs.length;i++) {
                    var o = orgs[i];
                    $rootScope.myOrgMap[o.id] = o;
                }
            });
        }
        $rootScope.loadProjectsForOrg = function (org) {
            var projects = Orgs.getProjectsInOrg({orgId: org.id});
            $rootScope.orgProjects[org.id] = projects;
            projects.$promise.then(function() {
                $rootScope.myProjects = $rootScope.myProjects.concat(projects);
                for (var i=0;i<projects.length; i++) {
                    var pro = projects[i];
                    $rootScope.myProjectMap[pro.id] = pro;
                }
            });
        };
        $rootScope.loadUsersInOrg = function (org) {
            $rootScope.orgUsers[org.id] = Orgs.getUsersInOrgWithCache({orgId: org.id});
        }
    }]);
