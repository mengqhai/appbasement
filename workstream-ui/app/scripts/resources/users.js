angular.module('resources.users', ['env'])
    .factory('Users', ['$resource', 'envConstants','envVars', function ($resource, envConstants, envVars) {
        var Users = $resource(envConstants.REST_BASE + '/users/:userIdBase64', {
            userIdBase64: '@userIdBase64',
            api_key: function() {
                return envVars.apiKey;
            }
        });
        return Users;
    }])