angular.module('controllers.tasklists', ['resources.tasklists', 'resources.projects', 'resources.tasks'])
    .controller('TaskListCreateFormController', ['$scope', 'Projects', '$modalInstance', '$rootScope',
        function ($scope, Projects, $modalInstance, $rootScope) {
            var taskList = {};
            $scope.taskList = taskList;
            $scope.projectSelection = {};
            $scope.myProjects = $scope.getMyProjects();
            $scope.createTaskList = function () {
                Projects.createTaskList({projectId: $scope.projectSelection.selected.id}, taskList, function(taskList) {
                    $rootScope.$broadcast('tasklists.create', taskList);
                    $modalInstance.close();
                })
            }
        }])