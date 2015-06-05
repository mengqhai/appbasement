angular.module('controllers.orgs', ['resources.orgs'])
    .controller('OrgAddFormController', ['$scope','Orgs', '$modalInstance', function($scope, Orgs, $modalInstance) {
        $scope.addBy = 'create';
        $scope.org = {};
    }])