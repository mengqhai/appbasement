angular.module('resources.users', ['env'])
    .factory('UserCache', ['$cacheFactory', function ($cacheFactory) {
        return $cacheFactory('UserCache');
    }])
    .factory('Users', ['$resource', 'envConstants', 'envVars', 'UserCache', function ($resource, envConstants, envVars, UserCache) {
        var Users = $resource(envConstants.REST_BASE + '/users/:userIdBase64', {
            userIdBase64: '@userIdBase64',
            api_key: envVars.getApiKey
        });

        Users.getUserPicUrl = function (userId) {
            return envConstants.REST_BASE + '/users/' + btoa(userId) + '/picture?api_key=' + envVars.getApiKey();
        }

        Users.getWithCache = function (param) {
            var user = UserCache.get(param.id);
            if (!user) {
                user = Users.get(param);
            }
            UserCache.put(param.id, user);
            return user;
        }
        return Users;
    }])