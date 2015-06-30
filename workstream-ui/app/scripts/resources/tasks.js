angular.module('resources.tasks', ['env', 'resources.utils'])
    .factory('Tasks', ['$resource', 'envConstants', 'envVars', 'XeditableResourcePromise', '$http', function ($resource, envConstants, envVars, xPromise, $http) {
        var paramDefault = {
            api_key: envVars.getApiKey
        };
        var homeUrl = envConstants.REST_BASE + '/tasks';
        var archHomeUrl = envConstants.REST_BASE + '/archives/tasks';
        var Tasks = $resource(homeUrl + '/:taskId', paramDefault, {
            getMyTasks: {
                url: homeUrl + '/_my',
                isArray: true
            },
            countMyTasks: {
                url: homeUrl + '/_my/_count'
            },
            countArchMyTasks: {
                url: archHomeUrl + '/_my/_count'
            },
            getArchTask: {
                url: archHomeUrl + '/:taskId'
            },
            getCreatedByMe: {
                url: homeUrl + '/_createdByMe',
                isArray: true
            },
            countCreatedByMe: {
                url: homeUrl + '/_createdByMe/_count'
            },
            countArchCreatedByMe: {
                url: archHomeUrl + '/_createdByMe/_count'
            },
            getMyCandidateTasks: {
                url: homeUrl + '/_myCandidate',
                isArray: true
            },
            countMyCandidateTasks: {
                url: homeUrl + '/_myCandidate/_count'
            },
            countArchMyCandidateTasks: {
                url: archHomeUrl + '/_myCandidate/_count'
            },
            getByListType: {
                url: homeUrl + '/:type',
                isArray: true
            },
            getArchByListType: {
                url: archHomeUrl + '/:type',
                isArray: true
            },
            getComments: {
                url: homeUrl + '/:taskId/comments',
                isArray: true
            },
            getEvents: {
                url: homeUrl + '/:taskId/events',
                isArray: true
            },
            addComment: {
                method: 'POST',
                url: homeUrl + '/:taskId/comments',
                headers: {
                    'Content-Type': 'text/plain'
                }
            },
            addArchComment: {
                method: 'POST',
                url: archHomeUrl + '/:taskId/comments',
                headers: {
                    'Content-Type': 'text/plain'
                }
            },
            getArchEvents: {
                url: envConstants.REST_BASE + '/archives/tasks/:taskId/events',
                isArray: true
            },
            getAttachments: {
                url: homeUrl + '/:taskId/attachments',
                isArray: true
            },
            getArchAttachments: {
                url: archHomeUrl + '/:taskId/attachments',
                isArray: true
            },
            patch: {
                method: 'PATCH',
                url: homeUrl + '/:taskId'
            },
            save: {
                method: 'POST',
                url: envConstants.REST_BASE + '/projects/:projectId/tasks'
            },
            claim: {
                method: 'PUT',
                url: homeUrl + '/:taskId/_claim'
            },
            getFormDef: {
                method: 'GET',
                url: homeUrl + '/:taskId/form'
            },
            getArchForm: {
                method: 'GET',
                url: envConstants.REST_BASE + '/archives/tasks/:taskId/form',
                isArray: true
            },
            completeForm: {
                method: 'PUT',
                url: homeUrl + '/:taskId/form'
            },
            complete: {
                method: 'PUT',
                url: homeUrl + '/:taskId/_complete'
            }
        });

        Tasks.create = function (task) {
            var projectId = task.projectId;
            return Tasks.save({projectId: projectId}, task);
        };

        Tasks.xedit = function (taskId, key, value) {
            var dataObj = {};
            dataObj[key] = value;
            var task = Tasks.patch({taskId: taskId}, dataObj);
            return xPromise.xeditablePromise(task);
        }
        Tasks.getAttachmentThumbUrl = function (attachmentId) {
            return envConstants.REST_BASE + '/attachments/' + attachmentId + '/thumb?api_key=' + envVars.getApiKey();
        }


        function uploadAttachment(taskId, file, urlBase) {
            // see https://uncorkedstudios.com/blog/multipartformdata-file-upload-with-angularjs
            var fd = new FormData();
            fd.append('file', file);
            var url = urlBase + '/' + taskId + '/attachments' + '?api_key=' + envVars.getApiKey();
            return $http.post(url, fd, {
                transformRequest: angular.identity,
                headers: {
                    'Content-Type': undefined
                }
            });
        }

        Tasks.uploadAttachment = function (taskId, file) {
            return uploadAttachment(taskId, file, homeUrl);
        }
        Tasks.uploadArchAttachment = function(taskId, file) {
            return uploadAttachment(taskId, file, archHomeUrl);
        }

        Tasks.createLoader = function (status) {
            if (status !== 'archived') {
                return {
                    countMyTasks: Tasks.countMyTasks,
                    countCreatedByMe: Tasks.countCreatedByMe,
                    countMyCandidateTasks: Tasks.countMyCandidateTasks,
                    getByListType: Tasks.getByListType
                };
            } else {
                return {
                    countMyTasks: Tasks.countArchMyTasks,
                    countCreatedByMe: Tasks.countArchCreatedByMe,
                    countMyCandidateTasks: Tasks.countArchMyCandidateTasks,
                    getByListType: Tasks.getArchByListType
                };
            }
        }
        return Tasks;
    }]);