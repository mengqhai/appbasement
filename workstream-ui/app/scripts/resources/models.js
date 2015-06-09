angular.module('resources.models', ['env'])
    .factory('Models', ['$resource', 'envConstants', 'envVars', function ($resource, envConstants, envVars) {
        var homeUrl = envConstants.REST_BASE + '/templatemodels/:modelId';
        var Models = $resource(homeUrl, {
            api_key: envVars.getApiKey
        }, {
            getRevisions: {
                method: 'GET',
                url: homeUrl + '/revisions',
                isArray: true
            }
        });
        return Models;
    }])