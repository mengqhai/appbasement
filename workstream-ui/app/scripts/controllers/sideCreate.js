angular.module('controllers.sideCreate', ['controllers.tasks', 'controllers.projects', 'controllers.tasklists'])
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
        }
        var taskListCfg = {
            templateUrl: 'views/taskListCreateForm.html',
            controller: 'TaskListCreateFormController',
            scope: $scope
        }

        $scope.openTaskDialog = function () {
            dialog = $modal.open(taskCfg)
        };
        $scope.openTaskListDialog = function() {
            dialog = $modal.open(taskListCfg);
        }
        $scope.openProjectDialog = function() {
            dialog = $modal.open(projectCfg);
        }
        $scope.closeDialog = function () {
            if (dialog) {
                dialog.dismiss('cancelCreateTask');
            }
        };

    }])