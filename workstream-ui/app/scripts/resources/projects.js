angular.module('resources.projects', [])
    .factory('Projects', ['$resource', 'envConstants','envVars', function($resource, envConstants, envVars) {
        var paramDefault = {
            api_key: envVars.getApiKey
        };
        var homeUrl = envConstants.REST_BASE + '/projects';
        var Projects = $resource(homeUrl + "/:projectId", paramDefault);
        return Projects;
    }]);