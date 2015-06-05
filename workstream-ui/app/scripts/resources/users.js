angular.module('resources.users', ['env'])
    .factory('UserCache', ['$cacheFactory', function ($cacheFactory) {
        return $cacheFactory('UserCache');
    }])
    .factory('Users', ['$resource', 'envConstants', 'envVars', 'UserCache', '$http', function ($resource, envConstants, envVars, UserCache, $http) {
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
            },
            create: {
                url: envConstants.REST_BASE + '/users',
                method: 'POST'
            }
        });

        Users.getUserPicUrl = function (userId) {
            return envConstants.REST_BASE + '/users/' + btoa(userId) + '/picture'; //?api_key=' + envVars.getApiKey();
        }

        Users.updatePic = function (userId, file) {
            // see https://uncorkedstudios.com/blog/multipartformdata-file-upload-with-angularjs


            var fd = new FormData();
            fd.append('file', file);
            var url = Users.getUserPicUrl(userId) + '?api_key='+envVars.getApiKey();
            console.log(envVars.getApiKey());
            return $http.post(url, fd, {
                transformRequest: angular.identity,
                headers: {
                    'Content-Type': undefined
                }
            });
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