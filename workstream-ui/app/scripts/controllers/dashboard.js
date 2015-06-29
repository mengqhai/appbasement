angular.module('controllers.dashboard', ['resources.notifications', 'resources.processes'])
    .controller('DashboardController', ['$scope', 'Notifications', '$state', function ($scope, Notifications, $state) {
        $scope.notifications = Notifications.getNotifications();
        $scope.getStateName = function (notification) {
            if (notification.targetType === 'COMMENT' ||
                notification.targetType === 'TASK') {
                return 'dashboard.notification.task({taskId:' + notification.targetId + ', notificationId:' + notification.id + '})';
            } else if (notification.targetType === 'PROCESS') {
                return 'dashboard.notification.process({processId:' + notification.targetId + ', notificationId:' + notification.id + '})';
            } else {
                return 'dashboard';
            }
        }
    }]);