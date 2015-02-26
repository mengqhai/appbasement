angular.module('controllers.project', ['resources.projects'])
    .controller('ProjectController', ['$scope', '$stateParams', 'Projects', function($scope, $stateParams, Projects) {
        $scope.project = $scope.getProject($stateParams.projectId);
        if (!$scope.project) {
            $scope.project = Projects.get({projectId: $stateParams.projectId});
        }
    }]);