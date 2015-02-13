angular.module('controllers.tasks', ['resources.tasks', 'ui.router', 'xeditable'])
    .controller('TasksController', ['$scope', 'Tasks', function ($scope, Tasks) {
        $scope.myTaskCount = 0;
        $scope.createdByMeCount = 0;
        $scope.candidateTaskCount = 0;

        $scope.loadCounts = function () {
            $scope.myTaskCount = Tasks.countMyTasks();
            $scope.createdByMeCount = Tasks.countCreatedByMe();
            $scope.candidateTaskCount = Tasks.countMyCandidateTasks();
        }

        //$scope.loadCounts();
    }])
    .controller('TaskListController', ['$scope', 'Tasks', '$stateParams', '$modal', function ($scope, Tasks, $stateParams, $modal) {
        $scope.params = $stateParams;
        $scope.tasks = [];
        $scope.loadByType = function () {
            $scope.tasks = Tasks.getByListType({type: $stateParams.listType});
        };
        $scope.loadByType();

        var dialog = null;
        $scope.openDialog = function (task) {
            dialog = $modal.open({
                templateUrl: 'views/taskForm.html',
                controller: 'TaskFormController',
                resolve: {
                    task: function () {
                        return task;
                    }
                },
                scope: $scope
            })
        };
        $scope.closeDialog = function () {
            if (dialog) {
                dialog.dismiss('cancelTask');
            }
        }
    }])
    .controller('TaskFormController', ['$scope', 'Tasks', 'Orgs', 'Users', 'task', function ($scope, Tasks, Orgs, Users, task) {
        $scope.task = task;
        $scope.org = Orgs.getWithCache({orgId: task.orgId});
        $scope.events = Tasks.getEvents({taskId: task.id});
        $scope.getUserPicUrl = Users.getUserPicUrl;
        $scope.updateTask = function (key, value) {
            var dataObj = {};
            dataObj[key] = value;
            return Tasks.patch({taskId: task.id}, dataObj);
        }
    }]);