angular.module('resources.processes', ['env'])
    .factory('Processes', ['$resource', 'envConstants', 'envVars', function ($resource, envConstants, envVars) {
        var homeUrl = envConstants.REST_BASE + '/processes/:processId';
        var Processes = $resource(homeUrl, {
            api_key: envVars.getApiKey
        }, {
            getStartedByMe: {
                method: 'GET',
                url: envConstants.REST_BASE + '/processes/_startedByMe',
                isArray: true
            },
            getInvolvesMe: {
                method: 'GET',
                url: envConstants.REST_BASE + '/processes/_involvedMe',
                isArray: true
            }
        })
        return Processes;
    }])