angular.module('controllers.dashboard', ['resources.notifications', 'resources.processes'])
    .controller('DashboardOverviewController', ['$scope', 'Orgs', 'Projects', function ($scope, Orgs, Projects) {
        $scope.myOrgs = $scope.getMyOrgs();

        $scope.projectTaskCounts = {};
        $scope.getProjectTaskCount = function (projectId) {
            if (!$scope.projectTaskCounts[projectId]) {
                $scope.projectTaskCounts[projectId] = Projects.countTasks({projectId: projectId});
            }
            return $scope.projectTaskCounts[projectId];
        }
    }])
    .controller('DashboardController', ['$scope', 'Notifications', '$state', function ($scope, Notifications, $state) {
        $scope.count = Notifications.countNotifications();
        $scope.notifications = Notifications.getNotifications();
        $scope.goToState = function (notification) {
            var stateName = 'dashboard';
            var params = {};
            if (notification.targetType === 'COMMENT') {
                stateName = 'dashboard.notification.task';
                params.taskId = notification.parentId;
                params.notificationId = notification.id;
            } else if (notification.targetType === 'TASK') {
                stateName = 'dashboard.notification.task';
                params.taskId = notification.targetId;
                params.notificationId = notification.id;
            } else if (notification.targetType === 'PROCESS') {
                stateName = 'dashboard.notification.process';
                params.processId = notification.targetId;
                params.notificationId = notification.id;
            }
            $state.go(stateName, params);
        }
        $scope.loadMore = function () {
            Notifications.getNotifications({first: $scope.notifications.length, max: 10}, function (moreNotifications) {
                $.merge($scope.notifications, moreNotifications);
            });
        }
        $scope.markAndGo = function (notification) {
            $scope.goToState(notification);
            Notifications.markRead({notificationId: notification.id}, null, function (newNotification) {
                notification.read = true;
            });
        }
        $scope.isNotificationActive = function (notification) {
            return $state.includes('dashboard.notification', {notificationId: notification.id});
        }
    }])
    .controller('DashboardTaskController', ['$scope', '$stateParams', 'Tasks', '$state', function ($scope, $stateParams, Tasks, $state) {
        $scope.taskId = $stateParams.taskId;
        $scope.task = Tasks.get({taskId: $scope.taskId}, function (task) {
            $scope.$broadcast('task.loaded', task);
        }, function (error) {
            if (error.status === 404) {
                Tasks.getArchTask({taskId: $scope.taskId}, function (archTask) {
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