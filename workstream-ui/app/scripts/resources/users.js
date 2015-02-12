angular.module('resources.users', ['env'])
    .factory('Users', ['$resource', 'envConstants', 'envVars', function ($resource, envConstants, envVars) {
        var Users = $resource(envConstants.REST_BASE + '/users/:userIdBase64', {
            userIdBase64: '@userIdBase64',
            api_key: envVars.getApiKey
        });

        Users.getUserPicUrl = function (userId) {
            return envConstants.REST_BASE + '/users/' + btoa(userId) + '/picture?api_key=' + envVars.getApiKey();
        }
        return Users;
    }])