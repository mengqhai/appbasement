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
        $scope.taskCount = Projects.countTasks({projectId: $stateParams.projectId});
        $scope.tasks = Projects.getTasks({projectId: $stateParams.projectId});
        $scope.loadMore = function () {
            Projects.getTasks(
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
    }]);