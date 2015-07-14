angular.module('controllers.projects', ['resources.projects', 'resources.tasklists'])
    .controller('ProjectController', ['$scope', '$stateParams', '$state', 'Projects', function ($scope, $stateParams, $state, Projects) {
        $scope.project = $scope.getProject($stateParams.projectId);
        if (!$scope.project) {
            $scope.project = Projects.get({projectId: $stateParams.projectId});
        }
        $scope.myMembership = Projects.getMyMembership({projectId: $stateParams.projectId});

        $scope.showTasks = function () {
            $state.go('.tasks', $stateParams);
        }

        $scope.updateProject = function (key, value) {
            return Projects.xedit($scope.project.id, key, value);
        };

        $scope.showMembershipTab = function () {
            return $scope.myMembership && ['ADMIN', 'PARTICIPANT'].indexOf($scope.myMembership.type) > -1;
        }
        $scope.canUpdateProject = function () {
            return $scope.myMembership && ['ADMIN'].indexOf($scope.myMembership.type) > -1;
        }
    }])
    .controller('ProjectCreateFormController', ['$scope', 'Projects', '$modalInstance', '$rootScope',
        function ($scope, Projects, $modalInstance, $rootScope) {
            var project = {};
            $scope.project = project;
            $scope.createError = null;
            $scope.orgs = $scope.getMyOrgs();
            $scope.createProject = function () {
                return Projects.create(project).$promise.then(function (project) {
                    if ($modalInstance) {
                        $rootScope.$broadcast('projects.create', project);
                        $modalInstance.close(project);
                    }
                }, function (error) {
                    $scope.createError = error.data.message;
                });
            };
        }])
    .controller('ProjectTaskListController', ['$scope', 'Projects', 'TaskLists', '$stateParams', '$modal', '$filter', 'Tasks',
        function ($scope, Projects, TaskLists, $stateParams, $modal, $filter, Tasks) {
            $scope.status = $stateParams.status ? $stateParams.status : 'active';
            var loader = Projects.createLoader($scope.status);

            /**
             * For task lists
             */
            var taskListsMap = {};
            $scope.taskListsMap = taskListsMap;
            function getTaskListId(parentId) {
                if (parentId && parentId.indexOf('list|') === 0) {
                    return parentId.substring(5);
                } else {
                    return 'non-listed';
                }
            }

            function setTaskPosition(task) {
                task.position = task.priority + task.createTime / 1000;
            }
            function mapTaskToList(task) {
                var taskListId = getTaskListId(task['parentId']);
                if (!taskListsMap[taskListId]) {
                    taskListsMap[taskListId] = [];
                }
                taskListsMap[taskListId].push(task);
                setTaskPosition(task);
                return taskListId;
            }

            function sortTaskPosition(taskListId) {
                var tasks = taskListsMap[taskListId];
                taskListsMap[taskListId] = $filter('orderBy')(tasks, 'position', true);
            }

            function sortAllTaskPosition() {
                Object.keys(taskListsMap).forEach(function (taskListId) {
                    sortTaskPosition(taskListId);
                })
            }

            function classifyTasksByList(tasks) {
                tasks.forEach(mapTaskToList);
                sortAllTaskPosition();
            }

            $scope.taskLists = Projects.getTaskLists({projectId: $stateParams.projectId}, function (taskLists) {
//                taskLists.forEach(function (taskList) {
//                    taskListsMap[taskList.id] = TaskLists.getTasks({taskListId: taskList.id});
//                })
            });
            $scope.getTasksInList = function (taskListId) {
                return taskListsMap[taskListId];
            }
            $scope.$on('tasklists.create', function (event, taskList) {
                $scope.taskLists.push(taskList);
            })
            $scope.$on('tasks.taskListChange', function (event, task, oldTaskListId) {
                var newTaskListId = mapTaskToList(task);
                sortTaskPosition(newTaskListId);
                if (!oldTaskListId) {
                    oldTaskListId = 'non-listed';
                }
                var oldIdx = taskListsMap[oldTaskListId].indexOf(task);
                taskListsMap[oldTaskListId].splice(oldIdx, 1);
            })
            $scope.$on('tasks.create', function (event, task) {
                var newTaskListId = mapTaskToList(task);
                sortTaskPosition(newTaskListId);
            })


            $scope.openTaskListDialog = function (taskList) {
                $modal.open({
                    templateUrl: 'views/taskListForm.html',
                    controller: 'TaskListDialogController',
                    resolve: {
                        taskList: function () {
                            return taskList;
                        }
                    },
                    scope: $scope
                })
            }


            $scope.taskCount = loader.countTasks({projectId: $stateParams.projectId});
            $scope.tasks = loader.getTasks({projectId: $stateParams.projectId, first: 0, max: 999}, function (tasks) {
                classifyTasksByList(tasks);
            });


            $scope.loadMore = function () {
                loader.getTasks(
                    {projectId: $stateParams.projectId,
                        first: $scope.tasks.length,
                        max: 10}, function (moreTasks) {
                        $.merge($scope.tasks, moreTasks);
                    })
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

            $scope.$on('tasks.delete', function (event, task) {
                // remove task
                for (var i = $scope.tasks.length - 1; i >= 0; i--) {
                    if ($scope.tasks[i].id === task.id) {
                        $scope.tasks.splice(i, 1);
                    }
                }
            });


            // For ui-sortable
            // http://api.jqueryui.com/sortable/
            var originalTasks;

            function calculatePriority(tasks, task) {
                var idx = tasks.indexOf(task);
                var beforeTask = idx !== 0 ? tasks[idx - 1] : null;
                var afterTask = (idx !== tasks.length - 1) ? tasks[idx + 1] : null;
                var newPosition;
                if (!beforeTask) {
                    newPosition = afterTask.position + 2;
                } else if (!afterTask) {
                    newPosition = beforeTask.position - 2;
                } else {
                    // in the middle between beforeTask.position and afterTask.position.
                    newPosition = (beforeTask.position + afterTask.position) / 2;
                }
                return  Math.round(newPosition - task.createTime / 1000);
            }

            $scope.sortableOptions = {
                handle: '> .draggableHandle',
                axis: 'y',
                // http://codepen.io/thgreasi/pen/MwQqdg
                start: function (event, ui) {
                    var task = ui.item.sortable.model;
                    // back up the original list for revert
                    originalTasks = taskListsMap[getTaskListId(task.parentId)].slice();
                },
                stop: function (event, ui) {
                    var task = ui.item.sortable.model;
                    var afterSort = taskListsMap[getTaskListId(task.parentId)];
                    if (afterSort.length <= 1) {
                        return;
                    }
                    console.log("dropped at " + afterSort.indexOf(task));
                    var newPriority = calculatePriority(afterSort, task);

                    Tasks.patch({taskId: task.id}, {priority: newPriority}, function() {
                        task.priority = newPriority;
                        setTaskPosition(task);
                    }, function() {
                        // on error
                        taskListsMap[getTaskListId(task.parentId)] = originalTasks;
                    })
                }
            }
        }])
    .controller('ProjectMemberController', ['$scope', 'Projects', '$stateParams', 'Users', 'Orgs', function ($scope, Projects, $stateParams, Users, Orgs) {
        var usersMap = {};

        function loadUserForMem(mem) {
            usersMap[mem.userId] = Users.getWithCache({userIdBase64: btoa(mem.userId)});
        }

        $scope.usersMap = usersMap;
        $scope.mems = Projects.getMemberships({projectId: $stateParams.projectId}, function (mems) {
            mems.forEach(function (mem) {
                loadUserForMem(mem);
            });
        });
        $scope.getUser = function (userId) {
            return usersMap[userId];
        }
        $scope.$on('membership.add', function (event, membership) {
            $scope.mems.unshift(membership);
            loadUserForMem(membership);
        });
        $scope.typeLabel = {
            ADMIN: 'Administrator',
            PARTICIPANT: 'Participant',
            GUEST: 'Guest'
        }
        var types = Object.keys($scope.typeLabel);
        $scope.getChangeTypes = function (mem) {
            return types.filter(function (type) {
                return type !== mem.type;
            })
        }

        $scope.deleteMembership = function (mem) {
            Projects.deleteMembership({projectId: mem.projectId, memId: mem.id}, function () {
                var idx = $scope.mems.indexOf(mem);
                $scope.mems.splice(idx, 1);
                delete usersMap[mem.userId];
            });
        }
        $scope.changeMembershipType = function (mem, newType) {
            Projects.updateMembership({projectId: mem.projectId, memId: mem.id}, {
                type: newType
            }, function () {
                mem.type = newType;
            });
        }
    }])
    .controller('ProjectMemberAddController', ['$scope', 'Projects', 'Orgs', function ($scope, Projects, Orgs) {
        function minusMems(userList) {
            var memIds = Object.keys($scope.usersMap);
            for (var i = userList.length - 1; i >= 0; i--) {
                if (memIds.indexOf(userList[i].id) !== -1) {
                    userList.splice(i, 1)
                }
            }
        }

        if ($scope.project && $scope.project.orgId) {
            $scope.userList = Orgs.getUsersInOrg({orgId: $scope.project.orgId}, minusMems);
        } else {
            $scope.project = Projects.get({projectId: $stateParams.projectId}, function (project) {
                $scope.userList = Orgs.getUsersInOrg({orgId: project.orgId}, minusMems);
            })
        }

        $scope.addSelection = {
            type: 'GUEST'
        };
        $scope.add = function () {
            Projects.addMembership({
                projectId: $scope.project.id
            }, {
                userId: $scope.addSelection.user.id,
                type: $scope.addSelection.type
            }, function (membership) {
                $scope.$emit("membership.add", membership);
            })
        }
    }]);