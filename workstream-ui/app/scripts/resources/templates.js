angular.module('resources.templates', ['env'])
    .factory('Templates', ['$resource', 'envConstants', 'envVars', function ($resource, envConstants, envVars) {
        var homeUrl = envConstants.REST_BASE + '/templates/:templateId';
        var Templates = $resource(homeUrl, {
            api_key: envVars.getApiKey
        }, {

        });
        Templates.getDiagramUrl = function (templateId) {
            return envConstants.REST_BASE + '/templates/' + templateId + '/diagram?api_key='+envVars.getApiKey();
        }
        return Templates;
    }])