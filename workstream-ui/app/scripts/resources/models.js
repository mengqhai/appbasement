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
            },
            getJson: {
                method: 'GET',
                url: homeUrl + '/workflow'
            },
            deploy: {
                method: 'POST',
                url: homeUrl + '/templates'
            },
            getTemplates: {
                method: 'GET',
                url: homeUrl + '/templates',
                isArray: true
            }
        });
        Models.getDiagramUrl = function(modelId) {
            if (!modelId) {
                return null;
            }
            return envConstants.REST_BASE + '/templatemodels/'+modelId+'/diagram?api_key=' + envVars.getApiKey();
        }
        return Models;
    }])