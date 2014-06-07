angular.module('resources.users', ['ngResource', 'env'])
    .factory('Users', ['$resource', 'envConstants', function ($resource, envConstants) {
        var Users = $resource(envConstants.REST_URL + '/users/:userId', {userId: '@id'}, {
            queryNotArray: {
                method: 'GET',
                isArray: false
            }
        });

        Users.isUsernameUnique = function (username, okCb) {
            return Users.queryNotArray({isUsernameUnique: username}, okCb);
        };


        return Users;
    }])