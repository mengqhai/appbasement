angular.module('resources.tasklists', [])
    .factory('TaskLists', ['$resource', 'envConstants', 'envVars', 'XeditableResourcePromise',
        function ($resource, envConstants, envVars, xPromise) {
            var paramDefault = {
                api_key: envVars.getApiKey
            };
            var homeUrl = envConstants.REST_BASE + '/tasklists';
            var TaskLists = $resource(homeUrl + ':taskListId', paramDefault, {
                getTasks: {
                    url: homeUrl + '/:taskListId/tasks',
                    isArray: true
                },
                patch: {
                    method: 'PATCH',
                    url: homeUrl + '/:taskListId'
                }
            });

            TaskLists.xedit = function (taskListId, key, value) {
                var dataObj = {};
                dataObj[key] = value;
                var taskList = TaskLists.patch({taskListId: taskListId}, dataObj);
                return xPromise.xeditablePromise(taskList);
            }
            return TaskLists;
        }])