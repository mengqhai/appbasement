angular.module('resources.notifications', ['env'])
    .factory('Notifications', ['$resource', 'envConstants', 'envVars', function ($resource, envConstants, envVars) {
        var homeUrl = envConstants.REST_BASE + '/notifications';
        var Notifications = $resource(homeUrl, {
            api_key: envVars.getApiKey
        }, {
            getNotifications: {
                url: homeUrl,
                isArray: true
            }
        })
        return Notifications;
    }])