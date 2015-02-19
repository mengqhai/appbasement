angular.module('controllers.tasks', ['resources.tasks', 'ui.router', 'xeditable', 'ui.select'])
    .controller('TasksController', ['$scope', 'Tasks', function ($scope, Tasks) {
        $scope.myTaskCount = 0;
        $scope.createdByMeCount = 0;
        $scope.candidateTaskCount = 0;

        $scope.loadCounts = function () {
            $scope.myTaskCount = Tasks.countMyTasks();
            $scope.createdByMeCount = Tasks.countCreatedByMe();
            $scope.candidateTaskCount = Tasks.countMyCandidateTasks();
        }

        $scope.$on('tasks.update.assignee', function (event, newAssignee) {
            $scope.myTaskCount = Tasks.countMyTasks();
        });
        $scope.$on('tasks.create', function (event, task) {
            if (task.assignee === $scope.getCurrentUserId()) {
                $scope.myTaskCount = Tasks.countMyTasks();
            }
            $scope.createdByMeCount = Tasks.countCreatedByMe();
        });
        $scope.$on('tasks.delete', function (event, task) {
            if (task.assignee === $scope.getCurrentUserId()) {
                $scope.myTaskCount = Tasks.countMyTasks();
            }
            $scope.createdByMeCount = Tasks.countCreatedByMe();
        });

        //$scope.loadCounts();
    }])
    .controller('TaskListController', ['$scope', 'Tasks', '$stateParams', '$modal', function ($scope, Tasks, $stateParams, $modal) {
        $scope.params = $stateParams;
        $scope.tasks = [];
        $scope.loadByType = function () {
            $scope.tasks = Tasks.getByListType({type: $stateParams.listType});
        };
        $scope.loadByType();

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
                    if (task.assignee === $scope.getCurrentUserId()) {
                        $scope.tasks.splice(0, 0, task);
                    }
                });
                $scope.$on('tasks.delete', function (event, task) {
                    if (task.assignee === $scope.getCurrentUserId()) {
                        removeTask(task);
                    }
                });
                break;
            case '_createdByMe':
                $scope.filterEx.creator = $scope.getCurrentUserId();
                $scope.$on('tasks.create', function (event, task) {
                    if (task.creator === $scope.getCurrentUserId()) {
                        $scope.tasks.splice(0, 0, task);
                    }
                });
                $scope.$on('tasks.delete', function (event, task) {
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
            $scope.events = Tasks.getEvents({taskId: task.id});
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
            $scope.userList = $scope.getOrgUsers(task.orgId).slice();
            $scope.userList.push(unassigned);
            $scope.assigneeError = null;
            $scope.changeAssignee = function (newAssignee) {
                $scope.updateTask('assignee', newAssignee.id).then(function (success) {
                    $scope.task.assignee = newAssignee.id;
                    $scope.$emit('tasks.update.assignee', newAssignee);
                }, function (error) {
                    $scope.assigneeError = error;
                });
            }


            // for due date
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

            // for delete
            $scope.delete = function () {
                Tasks.delete({taskId: task.id}, function (response) {
                    if ($modalInstance) {
                        $scope.$emit('tasks.delete', task);
                        $modalInstance.close();
                    }
                }, function (error) {
                    $scope.deleteError = error;
                });
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
        $scope.dueDateForPicker = task.dueDate;
        $scope.dueDateError = null;
        $scope.$watch('dueDateForPicker', function (newValue, oldValue) {
            if (newValue == oldValue) {
                return;
            }
            $scope.task.dueDate = $scope.dueDateForPicker;
        });

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