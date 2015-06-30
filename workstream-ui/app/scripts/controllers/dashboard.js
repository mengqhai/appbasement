angular.module('controllers.dashboard', ['resources.notifications', 'resources.processes'])
    .controller('DashboardController', ['$scope', 'Notifications', '$state', function ($scope, Notifications, $state) {
        $scope.notifications = Notifications.getNotifications();
        $scope.getStateName = function (notification) {
            if (notification.targetType === 'COMMENT') {
                return 'dashboard.notification.task({taskId:' + notification.parentId + ', notificationId:' + notification.id + '})';
            } else if (notification.targetType === 'TASK') {
                return 'dashboard.notification.task({taskId:' + notification.targetId + ', notificationId:' + notification.id + '})';
            } else if (notification.targetType === 'PROCESS') {
                return 'dashboard.notification.process({processId:' + notification.targetId + ', notificationId:' + notification.id + '})';
            } else {
                return 'dashboard';
            }
        }
    }])
    .controller('DashboardTaskController', ['$scope', '$stateParams', 'Tasks', '$state', function($scope, $stateParams, Tasks, $state) {
        $scope.taskId = $stateParams.taskId;
        $scope.task = Tasks.get({taskId: $scope.taskId}, function(task) {
            $scope.$broadcast('task.loaded', task);
        }, function(error) {
            if(error.status === 404) {
                Tasks.getArchTask({taskId:$scope.taskId}, function(archTask) {
                    $scope.task = archTask;
                    $scope.$broadcast('task.loaded', archTask);
                })
            }
        });

        function reload() {
            $state.reload();
        }
        $scope.$on('tasks.complete', reload);
        $scope.$on('tasks.claim', reload);
        $scope.$on('task.delete', reload);

    }]);