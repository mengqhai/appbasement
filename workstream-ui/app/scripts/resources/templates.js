angular.module('resources.templates', ['env'])
    .factory('Templates', ['$resource', 'envConstants', 'envVars', function ($resource, envConstants, envVars) {
        var homeUrl = envConstants.REST_BASE + '/templates/:templateId';
        var Templates = $resource(homeUrl, {
            api_key: envVars.getApiKey
        }, {
            getFormDef: {
                method: 'GET',
                url: homeUrl + '/form'
            },
            startByForm: {
                method: 'POST',
                url: homeUrl + '/form'
            },
            getProcesses: {
                method: 'GET',
                url: homeUrl + '/processes',
                isArray: true
            }
        });
        Templates.getDiagramUrl = function (templateId) {
            return envConstants.REST_BASE + '/templates/' + templateId + '/diagram?api_key=' + envVars.getApiKey();
        }
        return Templates;
    }])