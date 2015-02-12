angular.module('resources.tasks', ['env'])
    .factory('Tasks', ['$resource', 'envConstants', 'envVars', function ($resource, envConstants, envVars) {
        var paramDefault = {
            api_key: envVars.getApiKey
        };
        var homeUrl = envConstants.REST_BASE + '/tasks';
        var Tasks = $resource(homeUrl + '/:taskId', paramDefault, {
            getMyTasks: {
                url: homeUrl + '/_my',
                isArray: true
            },
            countMyTasks: {
                url: homeUrl + '/_my/_count'
            },
            getCreatedByMe: {
                url: homeUrl + '/_createdByMe',
                isArray: true
            },
            countCreatedByMe: {
                url: homeUrl + '/_createdByMe/_count'
            },
            getMyCandidateTasks: {
                url: homeUrl + '/_myCandidate',
                isArray: true
            },
            countMyCandidateTasks: {
                url: homeUrl + '/_myCandidate/_count'
            },
            getByListType: {
                url: homeUrl + '/:type',
                isArray: true
            },
            getComments: {
                url: homeUrl + '/:taskId/comments',
                isArray: true
            },
            getEvents: {
                url: homeUrl + '/:taskId/events',
                isArray: true
            }
        });
        return Tasks;
    }]);