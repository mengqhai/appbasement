angular.module('controllers.projects', ['resources.projects'])
    .controller('ProjectController', ['$scope', '$stateParams', '$state', 'Projects', function ($scope, $stateParams, $state, Projects) {
        $scope.project = $scope.getProject($stateParams.projectId);
        if (!$scope.project) {
            $scope.project = Projects.get({projectId: $stateParams.projectId});
        }

        $scope.showTasks = function () {
            $state.go('.tasks', $stateParams);
        }

        $scope.updateProject = function (key, value) {
            return Projects.xedit($scope.project.id, key, value);
        };
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
    .controller('ProjectTaskListController', ['$scope', 'Projects', '$stateParams', '$modal', function ($scope, Projects, $stateParams, $modal) {
        $scope.status = $stateParams.status ? $stateParams.status : 'active';
        var loader = Projects.createLoader($scope.status);


        $scope.taskCount = loader.countTasks({projectId: $stateParams.projectId});
        $scope.tasks = loader.getTasks({projectId: $stateParams.projectId});

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
            type: 'GUEST',
            typeLabel: {
                ADMIN: 'Administrator',
                PARTICIPANT: 'Participant',
                GUEST: 'Guest'
            }
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