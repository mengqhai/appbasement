angular.module('controllers.projects', ['resources.projects'])
    .controller('ProjectController', ['$scope', '$stateParams', '$state', 'Projects', function ($scope, $stateParams, $state, Projects) {
        $scope.project = $scope.getProject($stateParams.projectId);
        if (!$scope.project) {
            $scope.project = Projects.get({projectId: $stateParams.projectId});
        }
        ;

        $scope.showTasks = function () {
            $state.go('.tasks', $stateParams);
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
        }]);