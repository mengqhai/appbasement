angular.module('resources.projects', [])
    .factory('Projects', ['$resource', 'envConstants','envVars', function($resource, envConstants, envVars) {
        var paramDefault = {
            api_key: envVars.getApiKey
        };
        var homeUrl = envConstants.REST_BASE + '/projects';
        var Projects = $resource(homeUrl + "/:projectId", paramDefault, {
            save: {
                method: 'POST',
                url: envConstants.REST_BASE + '/orgs/:orgId/projects'
            },
            getTasks: {
                method: 'GET',
                url: homeUrl + "/:projectId/tasks",
                isArray: true
            }
        });
        Projects.create = function(project) {
            var orgId = project.orgId;
            delete project.orgId;
            return Projects.save({orgId: orgId}, project);
        };

        return Projects;
    }]);