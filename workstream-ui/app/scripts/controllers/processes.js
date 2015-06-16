angular.module('controllers.processes', ['resources.templates', 'resources.processes'])
    .controller('ProcessListController', ['$scope', '$stateParams', 'Processes', function($scope, $stateParams, Processes) {
        $scope.stateObj = {
            isStartedByMeOpen: false,
            isInvolvesMeOpen: false
        }
        $scope.startedByMe = null;
        $scope.involvesMe = null;

        $scope.$watch('stateObj.isStartedByMeOpen', function(newValue, oldValue) {
            if (newValue && !$scope.startedByMe) {
                $scope.startedByMe = Processes.getStartedByMe();
            }
        })
        $scope.$watch('stateObj.isInvolvesMeOpen', function(newValue, oldValue) {
            if (newValue && !$scope.involvesMe) {
                $scope.involvesMe = Processes.getInvolvesMe();
            }
        })
    }]);