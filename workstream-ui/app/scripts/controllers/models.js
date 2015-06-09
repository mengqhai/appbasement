angular.module('controllers.models', ['resources.models', 'resources.orgs'])
    .controller('ModelListController', ['$scope', 'Orgs', '$stateParams', 'models', function ($scope, Orgs, $stateParams, models) {
        //$scope.models = Orgs.getModelsInOrg({orgId: $stateParams.orgId});
        $scope.models = models; // from state resolve
    }])
    .controller('ModelDetailsController', ['$scope', 'envConstants', '$stateParams', 'Models', 'models', function ($scope, envConstants, $stateParams, Models, models) {
        function getModel(models) {
            for (i = 0; i < models.length; i++) {
                var model = models[i];
                if (model.id === $stateParams.modelId) {
                    $scope.model = model;
                    return model;
                }
            }
        }

        if (models.$resolved) {
            getModel(models);
        } else {
            models.$promise.then(getModel)
        }

        $scope.getDiagramUrl = function() {
            if (!$scope.model) {
                 return null;
            }
            return envConstants.REST_BASE + '/templatemodels/'+$scope.model.id+'/diagram';
        }

        $scope.revisionState = {
            isOpen:false
        }
        $scope.$watch('revisionState.isOpen', function(newValue, oldValue) {
            if (newValue === true && !$scope.revisions && $scope.model) {
                $scope.revisions = Models.getRevisions({modelId: $scope.model.id});
            }
        })
    }])