angular.module('controllers.orgSettings', [])
    .controller('OrgSettingsController', ['$scope', 'Orgs', function($scope, Orgs) {
        $scope.myAdminOrgs = Orgs.getMyAdminOrgs();
    }]);