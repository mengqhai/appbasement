angular.module('controllers.templates', ['resources.templates', 'resources.models'])
    .controller('TemplateListController', ['$scope', 'Templates', 'Orgs', 'templates', function ($scope, Templates, Orgs, templates) {
        $scope.templates = templates;
    }])
    .controller('TemplateDetailsController', ['$scope', '$stateParams', 'templates', 'Templates', 'Models', '$state',
        function ($scope, $stateParams, templates, Templates, Models, $state) {
            function getTemplate(templates) {
                for (i = 0; i < templates.length; i++) {
                    var template = templates[i];
                    if ($stateParams.templateId === template.id) {
                        $scope.template = template;
                        return template;
                    }
                }
            }

            if (templates.$resolved) {
                getTemplate(templates);
            } else {
                templates.$promise.then(getTemplate);
            }

            $scope.getDiagramUrl = function (templateId) {
                if (!$scope.template) {
                    return null;
                } else {
                    return Templates.getDiagramUrl(templateId);
                }
            }

            $scope.stateObj = {
                isUndeployConfirmOpen: false,
                isModelOpen: false,
                isStartOpen: false
            };

            /** start a process **/
            $scope.formObj = {};
            $scope.$watch('stateObj.isStartOpen', function(newValue, oldValue) {
                if (newValue) {
                    $scope.formDef = Templates.getFormDef({templateId: $scope.template.id});
                }
            });
            $scope.canStart = function(ngFormController) {
                return ngFormController.$valid;
            }
            $scope.startProcess = function() {
                Templates.startByForm({templateId: $scope.template.id}, $scope.formObj, function(process) {
                    console.log(process);
                });
            }

            /** undeploy **/
            $scope.undeployTemplate = function () {
                Templates.delete({templateId: $scope.template.id}, function () {
                    $state.reload().then(function () {
                        $state.go('^');
                    });
                    $scope.template = null;
                });
            }

            function loadModel (templateId) {
                if (!templateId) {
                    return;
                }
                var startIdx = 6;
                var endIdx = templateId.indexOf(':');
                var modelId = templateId.substring(startIdx, endIdx);
                if (!$scope.model) {
                    $scope.model = Models.get({modelId: modelId});
                    return $scope.model;
                }
            }
            $scope.$watch('stateObj.isModelOpen', function(newValue, oldValue) {
                if (newValue) {
                    loadModel($scope.template.id);
                }
            })
        }]);