angular.module('controllers.templates', ['resources.templates'])
    .controller('TemplateListController', ['$scope', 'Templates', 'Orgs', 'templates', function($scope, Templates, Orgs, templates) {
        $scope.templates = templates;
    }]);