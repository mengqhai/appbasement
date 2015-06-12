angular.module('controllers.templates', ['resources.templates'])
    .controller('TemplateListController', ['$scope', 'Templates', 'Orgs', 'templates', function($scope, Templates, Orgs, templates) {
        $scope.templates = templates;
    }])
    .controller('TemplateDetailsController', ['$scope', '$stateParams', 'templates', 'Templates', function($scope, $stateParams, templates, Templates) {
        function getTemplate(templates) {
            for (i=0;i<templates.length;i++) {
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

        $scope.getDiagramUrl = function(templateId) {
            if (!$scope.template) {
                return null;
            } else {
                return Templates.getDiagramUrl(templateId);
            }
        }
    }]);