angular.module('resources.users', ['env'])
    .factory('UserCache', ['$cacheFactory', function ($cacheFactory) {
        return $cacheFactory('UserCache');
    }])
    .factory('Users', ['$resource', 'envConstants', 'envVars', 'UserCache', function ($resource, envConstants, envVars, UserCache) {
        var homeUrl = envConstants.REST_BASE + '/users/:userIdBase64';
        var Users = $resource(homeUrl, {
            userIdBase64: '@userIdBase64',
            api_key: envVars.getApiKey
        }, {
            patch: {
                method: 'PATCH'
            },
            getInfo: {
                url: homeUrl + '/info'
            },
            setInfo: {
                url: homeUrl + '/info',
                method: 'PATCH'
            }
        });

        Users.getUserPicUrl = function (userId) {
            return envConstants.REST_BASE + '/users/' + btoa(userId) + '/picture'; //?api_key=' + envVars.getApiKey();
        }

        Users.getWithCache = function (param) {
            var user = UserCache.get(param.userIdBase64);
            if (!user) {
                user = Users.get(param);
            }
            UserCache.put(param.userIdBase64, user);
            return user;
        }
        return Users;
    }])