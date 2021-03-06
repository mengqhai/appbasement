angular.module('controllers.tasklists', ['resources.tasklists', 'resources.projects', 'resources.tasks'])
    .controller('TaskListCreateFormController', ['$scope', 'Projects', '$modalInstance', '$rootScope',
        function ($scope, Projects, $modalInstance, $rootScope) {
            var taskList = {};
            $scope.taskList = taskList;
            $scope.projectSelection = {};
            $scope.myProjects = $scope.getMyProjects();
            $scope.createTaskList = function () {
                Projects.createTaskList({projectId: $scope.projectSelection.selected.id}, taskList, function (taskList) {
                    $rootScope.$broadcast('tasklists.create', taskList);
                    $modalInstance.close();
                })
            }
        }])
    .controller('TaskListDialogController', ['$scope', 'Projects', 'TaskLists', '$rootScope', 'taskList', '$modalInstance',
        function ($scope, Projects, TaskLists, $rootScope, taskList, $modalInstance) {
            $scope.taskList = taskList;
            $scope.myProjects = $scope.getMyProjects();
            $scope.closeDialog = function () {
                $modalInstance.close();
            }

            $scope.updateTaskList = function (key, value) {
                return TaskLists.xedit(taskList.id, key, value);
            }
            $scope.onStartDateSelect = function (newValue, oldValue) {
                return $scope.updateTaskList('startTime', newValue);
            }
            $scope.onEndDateSelect = function (newValue, oldValue) {
                return $scope.updateTaskList('dueTime', newValue);
            }
        }])
    .controller('TaskListQuickAdderController', ['$scope', 'Projects', 'TaskLists', '$rootScope',
        function ($scope, Projects, TaskLists, $rootScope) {
            var projectId = $scope.project.id; // project id from the parent scope
            function initTaskList() {
                $scope.taskList = {};
            }

            initTaskList();
            $scope.addTaskList = function () {
                console.log($scope.taskList);
                console.log($scope.project);
                Projects.createTaskList({projectId: projectId}, $scope.taskList, function (taskList) {
                    $rootScope.$broadcast('tasklists.create', taskList);
                    initTaskList();
                })
            }
        }])