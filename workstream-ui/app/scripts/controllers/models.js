angular.module('controllers.models', ['resources.models', 'resources.orgs'])
    .controller('ModelListController', ['$scope', 'Orgs', '$stateParams', 'models', '$state', function ($scope, Orgs, $stateParams, models, $state) {
        //$scope.models = Orgs.getModelsInOrg({orgId: $stateParams.orgId});
        $scope.models = models; // from state resolve
        $scope.orgId = $stateParams.orgId;
    }])
    .controller('ModelDetailsController', ['$scope', 'envConstants', '$stateParams', '$state', 'Models', 'models', function ($scope, envConstants, $stateParams, $state, Models, models) {
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

        $scope.stateObj = {
            isRevisionOpen:false,
            isDeleteConfirmOpen: false,
            isDeployConfirmOpen: false,
            isEditorOpen: false,
            isTemplatesOpen: false
        }
        /** Revision history **/
        $scope.$watch('stateObj.isRevisionOpen', function(newValue, oldValue) {
            if (newValue === true && !$scope.revisions && $scope.model) {
                $scope.revisions = Models.getRevisions({modelId: $scope.model.id});
            }
        })

        /** For editing the model **/
        $scope.toggleEdit = function(event) {
            $scope.stateObj.isEditorOpen = !$scope.stateObj.isEditorOpen;
        }

        /** delete the model **/
        $scope.deleteModel = function() {
            Models.delete({modelId: $scope.model.id}, function() {
                //$scope.$emit('models.delete', $scope.model);
                $state.reload().then(function() {
                    $state.go('^');
                });
                $scope.model = null;
            });
        }

        /** deploy the model **/
        $scope.deployModel = function() {
            Models.deploy({modelId: $scope.model.id}, null, function(template) {
                console.log(template);
                $scope.stateObj.isDeployConfirmOpen = false;
                loadTemplates();
                $scope.stateObj.isTemplatesOpen = true;
            })
        }

        /** Deployed templates **/
        function loadTemplates() {
            $scope.templates = Models.getTemplates({modelId: $scope.model.id});
        }
        $scope.$watch('stateObj.isTemplatesOpen', function(newValue, oldValue) {
            if (newValue === true && !$scope.templates && $scope.model) {
                loadTemplates();
            }
        })

    }])
    .controller('ModelEditorController', ['$scope', 'Models', function($scope, Models) {
        $scope.editorModel = Models.get({modelId: $scope.model.id});
        $scope.modelJson = Models.getJson({modelId: $scope.model.id});
    }])