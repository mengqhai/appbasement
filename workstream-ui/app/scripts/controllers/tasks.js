angular.module('controllers.tasks', ['resources.tasks', 'resources.tasklists', 'resources.attachments', 'ui.router', 'xeditable', 'ui.select', 'ui.bootstrap.popover', 'directives.errorsrc', 'directives.processForm', 'filters.filesize'])
    .controller('TasksController', ['$scope', 'Tasks', '$stateParams', function ($scope, Tasks, $stateParams) {
        $scope.myTaskCount = 0;
        $scope.createdByMeCount = 0;
        $scope.candidateTaskCount = 0;

        $scope.status = $stateParams.status ? $stateParams.status : 'active';
        var loader = Tasks.createLoader($scope.status);

        $scope.loadCounts = function () {
            $scope.myTaskCount = loader.countMyTasks();
            $scope.createdByMeCount = loader.countCreatedByMe();
            $scope.candidateTaskCount = loader.countMyCandidateTasks();
        }

        $scope.$on('tasks.update.assignee', function (event, newAssignee) {
            $scope.myTaskCount = loader.countMyTasks();
        });
        $scope.$on('tasks.create', function (event, task) {
            if (task.assignee === $scope.getCurrentUserId()) {
                $scope.myTaskCount = loader.countMyTasks();
            }
            $scope.createdByMeCount = loader.countCreatedByMe();
        });
        $scope.$on('tasks.delete', function (event, task) {
            if (task.assignee === $scope.getCurrentUserId()) {
                $scope.myTaskCount = loader.countMyTasks();
            }
            $scope.createdByMeCount = loader.countCreatedByMe();
        });
        $scope.$on('tasks.claim', function (event, task) {
            $scope.myTaskCount.v++;
            $scope.candidateTaskCount.v--;
        });
        $scope.$on('tasks.complete', function (event, task) {
            // $scope.myTaskCount.v--;
            if (task.assignee === $scope.getCurrentUserId()) {
                $scope.myTaskCount = loader.countMyTasks();
            }
            $scope.createdByMeCount = loader.countCreatedByMe();
        });

        //$scope.loadCounts();
    }])
    .controller('TaskListController', ['$scope', 'Tasks', '$stateParams', '$modal', function ($scope, Tasks, $stateParams, $modal) {
        var loader = Tasks.createLoader($scope.status);
        $scope.params = $stateParams;
        $scope.tasks = [];

        function setTaskCount() {
            switch ($stateParams.listType) {
                case '_my':
                    $scope.taskCount = $scope.myTaskCount;
                    break;
                case '_createdByMe':
                    $scope.taskCount = $scope.createdByMeCount;
                    break;
                case '_myCandidate':
                    $scope.taskCount = $scope.candidateTaskCount;
                    break;
            }
        }

        var loadByType = function (onSuccess, first, max) {
            if (first === undefined || max === undefined) {
                first = 0;
                max = 10;
            }
            return loader.getByListType({type: $stateParams.listType, first: first, max: max}, onSuccess);
        };
        $scope.tasks = loadByType(setTaskCount);
        $scope.loadMore = function () {
            loadByType(function (moreTasks) {
                $.merge($scope.tasks, moreTasks);
            }, $scope.tasks.length, 10);
        }

        var removeTask = function (task) {
            var i = $scope.tasks.indexOf(task);
            if (i != -1) {
                $scope.tasks.splice(i, 1);
            }
        };

        $scope.filterEx = {};
        // This fitlerEx object can't get passed by $stataParams(as it's not defined in the URL template of the state),
        // so we have to perform a switch-cases here.
        switch ($stateParams.listType) {
            case '_my':
                $scope.filterEx.assignee = $scope.getCurrentUserId();
                $scope.$on('tasks.create', function (event, task) {
                    if (task.assignee === $scope.getCurrentUserId() && $scope.status === 'active') {
                        $scope.tasks.splice(0, 0, task);
                    }
                });
                $scope.$on('tasks.delete', function (event, task) {
                    if (task.assignee === $scope.getCurrentUserId() && $scope.status === 'active') {
                        removeTask(task);
                    }
                });
                $scope.$on('tasks.complete', function (event, task) {
                    removeTask(task);
                });
                break;
            case '_createdByMe':
                $scope.filterEx.creator = $scope.getCurrentUserId();
                $scope.$on('tasks.create', function (event, task) {
                    if (task.creator === $scope.getCurrentUserId() && $scope.status === 'active') {
                        $scope.tasks.splice(0, 0, task);
                    }
                });
                $scope.$on('tasks.delete', function (event, task) {
                    removeTask(task);
                });
                break;
            case '_myCandidate':
                $scope.$on('tasks.claim', function (event, task) {
                    removeTask(task);
                });
                break;
        }

        var dialog = null;
        $scope.openDialog = function (task) {
            dialog = $modal.open({
                templateUrl: 'views/taskForm.html',
                controller: 'TaskFormController',
                resolve: {
                    task: function () {
                        return task;
                    }
                },
                scope: $scope
            })
        };
        $scope.closeDialog = function () {
            if (dialog) {
                dialog.dismiss('cancelTask');
            }
        };

    }])
    .controller('TaskFormController', ['$scope', 'Tasks', 'Orgs', 'Users', '$modalInstance', 'task', 'Attachments',
        function ($scope, Tasks, Orgs, Users, $modalInstance, task, Attachments) {
            $scope.task = task;
            $scope.closeable = true;

            function closeDialog() {
                if ($modalInstance) {
                    $modalInstance.close();
                }
            }

            $scope.closeDialog = closeDialog;

            $scope.$on('tasks.complete', closeDialog);
            $scope.$on('tasks.claim', closeDialog);
            $scope.$on('tasks.delete', closeDialog);
        }])
    .controller('TaskDetailsController', ['$scope', 'Tasks', 'Orgs', 'Users', 'Attachments', 'Projects', 'TaskLists',
        function ($scope, Tasks, Orgs, Users, Attachments, Projects, TaskLists) {
            var task = $scope.task; // from parent scope

            var attachmentInvoker = null;
            var commentInvoker = null;
            // for assignee field
            var unassigned = {
                id: null,
                firstName: 'Unassigned'
            }

            function loadEverything(task) {
                loadOrg(task);
                loadEvents(task);
                loadAssignee(task);
                loadProject(task);
                loadProcessForm(task);
                prepareAttachmentInvoker(task);
                prepareCommentInvoker(task);
                countSubscription(task);
            }

            if (task.id !== undefined) {
                loadEverything(task);
            } else {
                $scope.$on('task.loaded', function (event, archTask) {
                    $scope.task = archTask;
                    loadEverything(archTask);
                })
            }


            function loadOrg(task) {
                $scope.org = Orgs.getWithCache({orgId: task.orgId});
            }

            // for events/comments
            function loadEvents(task) {
                var eventsGetter = task.endTime ? Tasks.getArchEvents : Tasks.getEvents
                $scope.events = eventsGetter({taskId: task.id});
            }

            $scope.getUserPicUrl = Users.getUserPicUrl;
            $scope.updateTask = function (key, value) {
                return Tasks.xedit(task.id, key, value);
            };

            function loadAssignee(task) {
                $scope.assignee = task.assignee ? Users.getWithCache({userIdBase64: btoa(task.assignee)}) : unassigned;
                if (task.orgId && !task.endTime) {
                    $scope.userList = $scope.getOrgUsers(task.orgId).slice();
                    $scope.userList.push(unassigned);
                }
            }


            $scope.assigneeError = null;
            $scope.changeAssignee = function (newAssignee) {
                $scope.updateTask('assignee', newAssignee.id).then(function (success) {
                    task.assignee = newAssignee.id;
                    $scope.$emit('tasks.update.assignee', newAssignee);
                }, function (error) {
                    $scope.assigneeError = error;
                });
            }
            function loadProject(task) {
                if (!task.projectId) {
                    return;
                }
                $scope.project = $scope.getProject(task.projectId);
                loadTaskList(task.projectId);
            }

            function loadTaskList(projectId) {
                $scope.taskLists = Projects.getTaskLists({projectId: projectId}, function (taskLists) {
                    taskLists.forEach(function (taskList) {
                        if ('list|' + taskList.id === $scope.task['parentId']) {
                            $scope.taskList = taskList;
                        }
                    });
                });
            }

            $scope.onSelectTaskList = function (taskList) {
                var oldTaskListId = null;
                if ($scope.task.parentId && $scope.task.parentId.indexOf('list|') === 0) {
                    oldTaskListId = $scope.task.parentId.substring(5);
                }
                TaskLists.addTask({taskListId: taskList.id, taskId: $scope.task.id}, null, function () {
                    $scope.task.parentId = 'list|' + taskList.id;
                    $scope.$emit('tasks.taskListChange', $scope.task, oldTaskListId)
                });
            }


            $scope.onSelectProject = function (project) {
                $scope.updateTask('projectId', project.id).then(function (success) {
                    task.projectId = project.id;
                    task.orgId = project.orgId;
                }, function (error) {
                    $scope.projectError = error;
                })
            }

            $scope.onDueDateSelect = function (newValue, oldValue) {
                return $scope.updateTask('dueDate', newValue);
            };

            // for delete
            $scope.delete = function () {
                Tasks.delete({taskId: task.id}, function (response) {
                    $scope.$emit('tasks.delete', task);
                }, function (error) {
                    $scope.deleteError = error.data.message;
                });
            }

            // for process tasks, claim the task
            $scope.claim = function () {
                Tasks.claim({taskId: $scope.task.id}, null, function () {
                    console.log('Claimed task ' + $scope.task.id);
                    $scope.$emit('tasks.claim', $scope.task);
                })
            }

            $scope.complete = function () {
                Tasks.complete({taskId: $scope.task.id}, null, function () {
                    $scope.$emit('tasks.complete', $scope.task);
                })
            }

            /** for process form **/
            function loadProcessForm(task) {
                if (task.processInstanceId && !task.endTime) {
                    $scope.formDef = Tasks.getFormDef({taskId: task.id});
                    $scope.formObj = {};
                    $scope.completeForm = function () {
                        Tasks.completeForm({taskId: task.id}, $scope.formObj).$promise.then(function () {
                            $scope.$emit('tasks.complete', task);
                        });
                    }
                } else {
                    $scope.archForm = Tasks.getArchForm({taskId: task.id});
                }
            }


            /**
             * For comment
             */
            $scope.commentObj = {comment: null}
            $scope.canComment = function (ngFormController) {
                if ($scope.fileFields.length === 0) {
                    return ngFormController.$valid;
                } else {

                    for (i = 0; i < $scope.fileFields.length; i++) {
                        var fileField = $scope.fileFields[i];
                        if (fileField['file'] === undefined) {
                            return false;
                        }
                    }
                    return true;
                }
            }


            function prepareCommentInvoker(task) {
                commentInvoker = task.endTime ? Tasks.addArchComment : Tasks.addComment;
            }

            $scope.addComment = function () {
                if (!$scope.commentObj.comment) {
                    return;
                }
                commentInvoker({taskId: $scope.task.id}, $scope.commentObj.comment, function (newComment) {
                    if ($scope.events) {
                        // the response is a comment entry
                        // here we have to make it look like an event entry
                        newComment.action = 'AddComment';
                        newComment.message = newComment['fullMessage'];
                        $scope.events.unshift(newComment)
                    }
                    $scope.commentObj.comment = null;
                });
            }

            /**
             * For attachment
             */
            $scope.stateObj = {
                isAttachmentOpen: false
            }


            function prepareAttachmentInvoker(task) {
                attachmentInvoker = task.endTime ? {
                    getAttachments: Tasks.getArchAttachments,
                    uploadAttachment: Tasks.uploadArchAttachment
                } : {
                    getAttachments: Tasks.getAttachments,
                    uploadAttachment: Tasks.uploadAttachment
                }
            }

            var loadAttachments = function () {
                $scope.attachments = attachmentInvoker.getAttachments({taskId: $scope.task.id});
            }
            $scope.$watch('stateObj.isAttachmentOpen', function (newValue, oldValue) {
                if (newValue && !$scope.attachments) {
                    loadAttachments();
                }
            })
            $scope.getAttachmentThumbUrl = Tasks.getAttachmentThumbUrl;

            /**
             * For adding attachment
             */
            $scope.fileFields = [];
            $scope.addFileField = function () {
                $scope.fileFields.push({});
            }
            $scope.removeFileField = function (fileField) {
                var index = $scope.fileFields.indexOf(fileField);
                $scope.fileFields.splice(index, 1);
            }
            $scope.addAttachments = function () {
                for (i = 0; i < $scope.fileFields.length; i++) {
                    var fileField = $scope.fileFields[i];
                    attachmentInvoker.uploadAttachment($scope.task.id, fileField.file).success(function (newAttachment) {
                        // make newAttachment look like an event
                        newAttachment.message = newAttachment.name;
                        newAttachment.action = 'AddAttachment';
                        newAttachment.time = new Date().getTime();
                        $scope.events.unshift(newAttachment);
                        $scope.fileFields.length = 0;
                        if ($scope.attachments) {
                            $scope.attachments.push(newAttachment);
                        }
                    })
                }
            }
            $scope.downloadAttachment = function (attachment) {
                var url = Attachments.getDownloadUrl(attachment.id);
                window.open(url);
            }

            /**
             * For subscription
             */
            function countSubscription(task) {
                if (task.endTime) {
                    return;
                }
                $scope.subCount = Tasks.countSubscriptions({taskId: task.id});
            }

            $scope.$on('subscription.remove', function () {
                $scope.subCount.v--;
            })
            $scope.$on('subscription.create', function () {
                $scope.subCount.v++;
            })
        }])
    .controller('TaskSubscriptionsController', ['$scope', 'Tasks', function ($scope, Tasks) {
        $scope.subs = Tasks.getSubscriptions({taskId: $scope.task.id});
        $scope.currentUser = $scope.getCurrentUserId();
        $scope.canSubscribe = function () {
            var mine = $scope.subs.filter(function (sub) {
                if (sub.userId === $scope.currentUser) {
                    return true;
                }
            });
            return $scope.subs.$resolved && mine.length < 1;
        }
        $scope.unsubscribe = function () {
            Tasks.unsubscribe({taskId: $scope.task.id}, function () {
                var idx = -1;
                for (i = 0; i < $scope.subs.length; i++) {
                    if ($scope.subs[i].userId === $scope.currentUser) {
                        idx = i;
                        break;
                    }
                }
                if (idx > -1) {
                    $scope.subs.splice(idx, 1);
                    $scope.$emit('subscription.remove');
                }
            })
        }

        $scope.subscribe = function () {
            Tasks.subscribe({taskId: $scope.task.id, userIdBase64: btoa($scope.currentUser)}, null, function (newSub) {
                $scope.subs.unshift(newSub);
                $scope.$emit('subscription.create');
            })
        }
    }])
    .controller('TaskCreateFormController', ['$scope', 'Tasks', '$modalInstance', '$rootScope', function ($scope, Tasks, $modalInstance, $rootScope) {
        var task = {};
        $scope.task = task;
        $scope.myProjects = $scope.getMyProjects();
        $scope.onSelectProject = function (project) {
            $scope.task.projectId = project.id;
            $scope.task.orgId = project.orgId;
            refreshUserList($scope.task.orgId);
        };


        // for assignee
        var unassigned = {
            id: null,
            firstName: 'Unassigned'
        }
        $scope.assignee = task.assignee ? Users.getWithCache({userIdBase64: btoa(task.assignee)}) : unassigned;
        var refreshUserList = function (orgId) {
            $scope.userList = orgId ? $scope.getOrgUsers(orgId).slice() : [];
            $scope.userList.push(unassigned);
        };
        refreshUserList(task.orgId);

        $scope.assigneeError = null;
        $scope.changeAssignee = function (newAssignee) {
            task.assignee = newAssignee.id;
        };


        // for due date
        $scope.open = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.opened = true;
        };

        /*
         $scope.dueDateForPicker = task.dueDate;
         $scope.dueDateError = null;
         $scope.$watch('dueDateForPicker', function (newValue, oldValue) {
         if (newValue == oldValue) {
         return;
         }
         $scope.task.dueDate = $scope.dueDateForPicker;
         });
         */

        $scope.createTask = function () {
            return Tasks.create(task).$promise.then(function (task) {
                if ($modalInstance) {
                    $rootScope.$broadcast('tasks.create', task);
                    $modalInstance.close(task);
                }
            }, function (error) {
                $scope.createError = error.data.message;
            });
        }
    }])
    .controller('TaskQuickAdderController', ['$scope', 'Tasks', function ($scope, Tasks) {
        function initTask() {
            $scope.task = {
                projectId: $scope.project.id
            };
            if ($scope.taskList) {
                $scope.task.parentId = 'list|' + $scope.taskList.id
            }
        }

        initTask();
        $scope.addTask = function () {
            Tasks.create($scope.task).$promise.then(function (task) {
                $scope.$emit('tasks.create', task);
                initTask();
            });
        }
    }]);