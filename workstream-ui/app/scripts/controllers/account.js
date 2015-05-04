angular.module('controllers.account', [])
    .controller('AccountSettingsController', ['$scope', function ($scope) {
        $scope.currentUser = $scope.getCurrentUser();
    }]);