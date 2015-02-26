angular.module('controllers.project', ['resources.projects'])
    .controller('ProjectController', ['$scope', '$stateParams', '$state', 'Projects', function($scope, $stateParams, $state, Projects) {
        $scope.project = $scope.getProject($stateParams.projectId);
        if (!$scope.project) {
            $scope.project = Projects.get({projectId: $stateParams.projectId});
        };

        $scope.showTasks = function() {
            $state.go('.tasks', $stateParams);
        }
    }]);