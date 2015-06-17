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
    }])
    .controller('ProcessDetailsController', ['$scope', '$stateParams', 'Processes', function($scope, $stateParams, Processes) {
        function getArchProcess(processId, archProcesses) {
            for (var i=0;i<archProcesses.length;i++) {
                var archProcess = archProcesses[i];
                if (archProcess.id === processId) {
                    return archProcess;
                }
            }
        }
        $scope.archProcess = Processes.getArchive({processId: $stateParams.processId});
        $scope.process = Processes.get({processId: $stateParams.processId});
        $scope.getDiagramUrl = function(processId) {
            if (!processId) {
                return null;
            }
            return Processes.getDiagramUrl(processId);
        }
    }]);