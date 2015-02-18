angular.module('controllers.sideCreate', ['controllers.tasks'])
    .controller('SideCreateController', ['$scope', '$modal', function ($scope, $modal) {
        var dialog = null;
        $scope.openDialog = function () {
            dialog = $modal.open({
                templateUrl: 'views/taskCreateForm.html',
                controller: 'TaskCreateFormController',
                scope: $scope
            })
        };
        $scope.closeDialog = function () {
            if (dialog) {
                dialog.dismiss('cancelCreateTask');
            }
        };
    }])