angular.module('resources.users', ['env'])
    .factory('Users', ['$resource', 'envConstants', function ($resource, envConstants) {
        var Users = $resource(envConstants.REST_BASE + '/users/:userIdBase64', {
            userIdBase64: '@userIdBase64'
        });
        return Users;
    }])