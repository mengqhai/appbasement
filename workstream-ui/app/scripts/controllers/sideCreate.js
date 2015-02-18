angular.module('controllers.sideCreate', [])
    .controller('SideCreateController', ['$scope', '$modal', function ($scope, $modal) {
        var dialog = null;
        $scope.openDialog = function () {
            dialog = $modal.open({
                templateUrl: 'views/taskCreateForm.html',
                scope: $scope
            })
        };
        $scope.closeDialog = function () {
            if (dialog) {
                dialog.dismiss('cancelCreateTask');
            }
        };
        $scope.task = {};
    }])