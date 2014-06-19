angular.module('services.notifications', [])
    // https://github.com/marcorinck/angular-growl
    .factory('notifications', ['$rootScope', function ($rootScope) {
        var notifications = {
            'STICKY': [],
            'ROUTE_CURRENT': [],
            'ROUTE_NEXT': []
        };

        var notificationService = {};

        var addNotification = function(notificationsArray, notificationObj) {
            if (!angular.isObject(notificationObj)) {
                throw new Error('Only object can be added to the notification service');
            };
            notificationsArray.push(notificationObj);
            return notificationObj;
        };

        $rootScope.$on('$routeChangeSuccess', function() {
            notifications.ROUTE_CURRENT.length = 0;
            notifications.ROUTE_CURRENT = angular.copy(notifications.ROUTE_NEXT);
            notifications.ROUTE_NEXT.length = 0;
        });

        notificationService.getCurrent = function() {
            return [].concat(notifications.STICKY, notifications.ROUTE_CURRENT);
        };

        notificationService.pushSticky = function(notification) {
            return addNotification(notifications.STICKY, notification);
        };

        notificationService.pushForCurrentRoute = function(notification) {
            return addNotification(notifications.ROUTE_CURRENT, notification);
        };

        notificationService.pushForNextRoute = function(notification) {
            return addNotification(notifications.ROUTE_NEXT, notification);
        };

        notificationService.remove = function(notification) {
            angular.forEach(notifications, function(notificationsByType) {
                var idx = notificationsByType.indexOf(notification);
                if (idx > -1) {
                    notificationsByType.splice(idx, 1);
                    return notification;
                }
            });
        };

        notificationService.removeAll = function() {
            angular.forEach(notifications, function(notificationsByType) {
                notificationsByType.length = 0;
            });
        };

        return notificationService;
    }])