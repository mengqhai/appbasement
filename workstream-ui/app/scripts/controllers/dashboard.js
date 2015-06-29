angular.module('controllers.dashboard', ['resources.notifications'])
    .controller('DashboardController', ['$scope', 'Notifications', function($scope, Notifications) {
        $scope.notifications = Notifications.getNotifications();
    }]);