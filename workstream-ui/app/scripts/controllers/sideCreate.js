angular.module('controllers.sideCreate', ['controllers.tasks', 'controllers.projects'])
    .controller('SideCreateController', ['$scope', '$modal', function ($scope, $modal) {
        var dialog = null;
        var projectCfg = {
            templateUrl: 'views/projectCreateForm.html',
            controller: 'ProjectCreateFormController',
            scope: $scope
        };
        var taskCfg = {
            templateUrl: 'views/taskCreateForm.html',
            controller: 'TaskCreateFormController',
            scope: $scope
        };
        $scope.openTaskDialog = function () {
            dialog = $modal.open(taskCfg)
        };
        $scope.openProjectDialog = function() {
            dialog = $modal.open(projectCfg);
        }
        $scope.closeDialog = function () {
            if (dialog) {
                dialog.dismiss('cancelCreateTask');
            }
        };
    }])