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
    .factory('envCache', ['Orgs', 'envVars', function (Orgs, envVars) {
        var myOrgs = [];
        var myOrgMap = {};
        var myProjects = [];
        var myProjectMap = {};
        var orgProjects = {};
        var orgUsers = {};

        var envCache = {
            clearAll: function () {
                myOrgs.length = 0;
                myOrgMap = {};
                myProjects.length = 0;

                myProjectMap = {};
                orgProjects = {};
                orgUsers = {};
            },
            getProject: function (projectId) {
                return myProjectMap[projectId];
            },
            getOrg: function (orgId) {
                return myOrgMap[orgId];
            },
            getMyOrgs: function () {
                return myOrgs;
            },
            getOrgProjects: function (orgId) {
                return orgProjects[orgId];
            },
            getMyProjects: function () {
                return myProjects;
            },
            getOrgUsers: function (orgId) {
                return orgUsers[orgId];
            }
        };

        envCache.loadMyOrgs = function () {
            if (!envVars.isLoggedIn()) {
                return;
            }
            var orgs = Orgs.getMyOrgs();
            myOrgs = orgs;
            orgs.$promise.then(function () {
                for (var i = 0; i < orgs.length; i++) {
                    var o = orgs[i];
                    myOrgMap[o.id] = o;
                    envCache.loadProjectsForOrg(o);
                    envCache.loadUsersInOrg(o);
                }
            });
        };

        envCache.loadProjectsForOrg = function (org) {
            var projects = Orgs.getProjectsInOrg({orgId: org.id});
            orgProjects[org.id] = projects;
            projects.$promise.then(function () {
                var temp = myProjects.filter(function(value,index, array) {
                    if (value.orgId == org.id) {
                        return false;
                    } else {
                        return true;
                    }
                });
                myProjects.length = 0;
                myProjects = myProjects.concat(temp);
                myProjects = myProjects.concat(projects);
                for (var i = 0; i < projects.length; i++) {
                    var pro = projects[i];
                    myProjectMap[pro.id] = pro;
                }
            });
        };

        envCache.loadUsersInOrg = function (org) {
            orgUsers[org.id] = Orgs.getUsersInOrgWithCache({orgId: org.id});
        };

        envCache.initAll = function () {
            envCache.clearAll();
            envCache.loadMyOrgs();
        };

        return envCache;
    }])
    .run(['$rootScope', 'envVars', 'envCache', function ($rootScope, envVars, envCache) {
        $rootScope.isLoggedIn = envVars.isLoggedIn;
        $rootScope.getCurrentUser = envVars.getCurrentUser;
        $rootScope.getCurrentUserId = envVars.getCurrentUserId;

        $rootScope.loadMyOrgs = envCache.loadMyOrgs;
        $rootScope.loadProjectsForOrg = envCache.loadProjectsForOrg;
        $rootScope.loadUsersInOrg = envCache.loadUsersInOrg;
        $rootScope.getMyOrgs = envCache.getMyOrgs;
        $rootScope.getProject = envCache.getProject;
        $rootScope.getOrg = envCache.getOrg;
        $rootScope.getOrgProjects = envCache.getOrgProjects;
        $rootScope.getMyProjects = envCache.getMyProjects;
        $rootScope.getOrgUsers = envCache.getOrgUsers;
        $rootScope.initCache = envCache.initAll;

        $rootScope.$on('projects.create', function(event, project) {
            envCache.loadProjectsForOrg({id:project.orgId});
            //envCache.initAll();
        });

        $rootScope.$on('login', function () {
            envCache.initAll();
        });

        $rootScope.$on('logout', function() {
            envCache.clearAll();
        });
    }]);
