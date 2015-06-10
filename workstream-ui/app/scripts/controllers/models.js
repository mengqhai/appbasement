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

        $scope.getDiagramUrl = function(modelId) {
            if (!$scope.model) {
                 return null;
            }
            return Models.getDiagramUrl(modelId);
        }

        $scope.revisionState = {
            isOpen:false
        }
        $scope.$watch('revisionState.isOpen', function(newValue, oldValue) {
            if (newValue === true && !$scope.revisions && $scope.model) {
                $scope.revisions = Models.getRevisions({modelId: $scope.model.id});
            }
        })

        /** For editing the model **/
        $scope.editState = {
            isOpen: false
        }
        $scope.toggleEdit = function(event) {
            $scope.editState.isOpen = !$scope.editState.isOpen;
        }
    }])
    .controller('ModelEditorController', ['$scope', 'Models', function($scope, Models) {
        $scope.editorModel = Models.get({modelId: $scope.model.id});
        $scope.modelJson = Models.getJson({modelId: $scope.model.id});
    }])