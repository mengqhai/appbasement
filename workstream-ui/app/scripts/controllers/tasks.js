angular.module('controllers.tasks', ['resources.tasks', 'ui.router', 'xeditable', 'ui.select', 'directives.errorsrc', 'directives.processForm'])
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
        $scope.loadMore = function() {
            loadByType(function(moreTasks) {
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
    .controller('TaskFormController', ['$scope', 'Tasks', 'Orgs', 'Users', '$q', '$modalInstance', 'task',
        function ($scope, Tasks, Orgs, Users, $q, $modalInstance, task) {
            $scope.task = task;
            $scope.org = Orgs.getWithCache({orgId: task.orgId});
            //$scope.myOrgs = Orgs.getMyOrgsWithCache();

            // for events/comments
            var eventsGetter = task.endTime ? Tasks.getArchEvents : Tasks.getEvents
            $scope.events = eventsGetter({taskId: task.id});
            $scope.getUserPicUrl = Users.getUserPicUrl;
            $scope.updateTask = function (key, value) {
                return Tasks.xedit($scope.task.id, key, value);
            };

            // for assignee field
            var unassigned = {
                id: null,
                firstName: 'Unassigned'
            }
            $scope.assignee = task.assignee ? Users.getWithCache({userIdBase64: btoa(task.assignee)}) : unassigned;
            if (task.orgId && !task.endTime) {
                $scope.userList = $scope.getOrgUsers(task.orgId).slice();
                $scope.userList.push(unassigned);
            }

            $scope.assigneeError = null;
            $scope.changeAssignee = function (newAssignee) {
                $scope.updateTask('assignee', newAssignee.id).then(function (success) {
                    $scope.task.assignee = newAssignee.id;
                    $scope.$emit('tasks.update.assignee', newAssignee);
                }, function (error) {
                    $scope.assigneeError = error;
                });
            }

            $scope.project = $scope.getProject(task.projectId);
            //$scope.myProjects = $scope.getMyProjects();
            $scope.onSelectProject = function (project) {
                $scope.updateTask('projectId', project.id).then(function (success) {
                    $scope.task.projectId = project.id;
                    $scope.task.orgId = project.orgId;
                }, function (error) {
                    $scope.projectError = error;
                })
            }


            // for due date
            /*
             $scope.open = function ($event) {
             $event.preventDefault();
             $event.stopPropagation();
             $scope.opened = true;
             };
             $scope.dueDateForPicker = task.dueDate;
             $scope.dueDateError = null;
             $scope.$watch('dueDateForPicker', function (newValue, oldValue) {
             if (newValue == oldValue) {
             return;
             }
             $scope.updateTask('dueDate', newValue).then(function (sucess) {
             $scope.task.dueDate = $scope.dueDateForPicker;
             }, function (error) {
             $scope.dueDateError = error;
             })
             });
             */
            $scope.onDueDateSelect = function (newValue, oldValue) {
                return $scope.updateTask('dueDate', newValue);
            };


            var closeDialog = function () {
                if ($modalInstance) {
                    $modalInstance.close();
                }
            }
            $scope.closeDialog = closeDialog;

            // for delete
            $scope.delete = function () {
                Tasks.delete({taskId: task.id}, function (response) {
                    $scope.$emit('tasks.delete', task);
                    closeDialog();
                }, function (error) {
                    $scope.deleteError = error.data.message;
                });
            }

            // for process tasks, claim the task
            $scope.claim = function () {
                Tasks.claim({taskId: task.id}, null, function () {
                    console.log('Claimed task ' + task.id);
                    $scope.$emit('tasks.claim', task);
                    closeDialog();
                })
            }

            $scope.complete = function () {
                Tasks.complete({taskId: task.id}, null, function () {
                    console.log('Completed task ' + task.id);
                    $scope.$emit('tasks.complete', task);
                    closeDialog();
                })
            }


            $scope.$on('tasks.complete', closeDialog);
            $scope.$on('tasks.claim', closeDialog);

            /** for process form **/
            if (!task.endTime) {
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
    }]);