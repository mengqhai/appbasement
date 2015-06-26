angular.module('resources.projects', [])
    .factory('Projects', ['$resource', 'envConstants','envVars','XeditableResourcePromise', function($resource, envConstants, envVars, xPromise) {
        var paramDefault = {
            api_key: envVars.getApiKey
        };
        var homeUrl = envConstants.REST_BASE + '/projects';
        var Projects = $resource(homeUrl + '/:projectId', paramDefault, {
            save: {
                method: 'POST',
                url: envConstants.REST_BASE + '/orgs/:orgId/projects'
            },
            getTasks: {
                method: 'GET',
                url: homeUrl + '/:projectId/tasks',
                isArray: true
            },
            countTasks: {
                url: homeUrl + '/:projectId/tasks/_count'
            },
            patch: {
                method: 'PATCH',
                url: homeUrl + '/:projectId'
            }
        });
        Projects.create = function(project) {
            var orgId = project.orgId;
            delete project.orgId;
            return Projects.save({orgId: orgId}, project);
        };
        Projects.xedit = function(projectId, key, value) {
            var dataObj = {};
            dataObj[key] = value;
            var pro = Projects.patch({projectId: projectId}, dataObj);
            return xPromise.xeditablePromise(pro);
        };

        return Projects;
    }]);