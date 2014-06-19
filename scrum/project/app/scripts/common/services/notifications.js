angular.module('services.notifications', [])
    // https://github.com/marcorinck/angular-growl
    .factory('notifications', ['$rootScope', function ($rootScope) {
        var notificationService = {};

        var addNotification = function(notificationsArray, notificationObj) {
            if (!angular.isObject(notificationObj)) {
                throw new Error('Only object can be added to the notification service');
            };
            notificationsArray.push(notificationObj);
            return notificationObj;
        };
        notificationService.current=[];

        notificationService.pushForCurrentRoute = function(notification) {
            return addNotification(notificationService.current, notification);
        };
        return notificationService;
    }])