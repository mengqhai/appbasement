angular.module('resources.tasklists', [])
    .factory('TaskLists', ['$resource', 'envConstants', 'envVars', function ($resource, envConstants, envVars) {
        var paramDefault = {
            api_key: envVars.getApiKey
        };
        var homeUrl = envConstants.REST_BASE + '/tasklists';
        var TaskLists = $resource(homeUrl + ':taskListId', paramDefault, {
            getTasks: {
                url: homeUrl + '/:taskListId/tasks',
                isArray: true
            }
        });
        return TaskLists;
    }])