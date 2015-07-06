angular.module('resources.projects', [])
    .factory('Projects', ['$resource', 'envConstants', 'envVars', 'XeditableResourcePromise', function ($resource, envConstants, envVars, xPromise) {
        var paramDefault = {
            api_key: envVars.getApiKey
        };
        var homeUrl = envConstants.REST_BASE + '/projects';
        var archHomeUrl = envConstants.REST_BASE + '/archives/projects';
        var memHomeUrl = homeUrl + '/:projectId/memberships';
        var Projects = $resource(homeUrl + '/:projectId', paramDefault, {
            save: {
                method: 'POST',
                url: envConstants.REST_BASE + '/orgs/:orgId/projects'
            },
            getTasks: {
                url: homeUrl + '/:projectId/tasks',
                isArray: true
            },
            getArchTasks: {
                url: archHomeUrl + '/:projectId/tasks',
                isArray: true
            },
            countArchTasks: {
                url: archHomeUrl + '/:projectId/tasks/_count'
            },
            countTasks: {
                url: homeUrl + '/:projectId/tasks/_count'
            },
            patch: {
                method: 'PATCH',
                url: homeUrl + '/:projectId'
            },
            getMemberships: {
                url: memHomeUrl,
                isArray: true
            },
            getMyMembership: {
                url: memHomeUrl + "/_my"
            },
            addMembership: {
                method: 'POST',
                url: memHomeUrl
            },
            deleteMembership: {
                method: 'DELETE',
                url: memHomeUrl + '/:memId'
            },
            updateMembership: {
                method: 'PATCH',
                url: memHomeUrl + '/:memId'
            }
        });
        Projects.create = function (project) {
            var orgId = project.orgId;
            delete project.orgId;
            return Projects.save({orgId: orgId}, project);
        };
        Projects.xedit = function (projectId, key, value) {
            var dataObj = {};
            dataObj[key] = value;
            var pro = Projects.patch({projectId: projectId}, dataObj);
            return xPromise.xeditablePromise(pro);
        };

        Projects.createLoader = function (status) {
            if (status !== 'archived') {
                return {
                    getTasks: Projects.getTasks,
                    countTasks: Projects.countTasks
                }
            } else {
                return {
                    getTasks: Projects.getArchTasks,
                    countTasks: Projects.countArchTasks
                }
            }
        }

        return Projects;
    }]);