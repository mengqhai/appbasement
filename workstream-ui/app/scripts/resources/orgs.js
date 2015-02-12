angular.module('resources.orgs', ['env'])
    .factory('Orgs', ['$resource', 'envConstants', 'envVars', function ($resource, envConstants, envVars) {
        var paramDefault = {
            api_key: envVars.getApiKey
        };
        var Orgs = $resource(envConstants.REST_BASE + '/orgs/:orgId', paramDefault, {
            getMyOrgs: {
                method: 'GET',
                isArray: true
            },
            getProjectsInOrg: {
                url: envConstants.REST_BASE + '/orgs/:orgId/projects',
                method: 'GET',
                isArray: true
            }
        });
        return Orgs;
    }]);