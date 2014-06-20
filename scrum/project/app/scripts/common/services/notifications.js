angular.module('services.notifications', ['angular-growl'])
    // https://github.com/marcorinck/angular-growl
    .config(['growlProvider', function(growlProvider) {
        growlProvider.globalTimeToLive(5000);
    }])
    .factory('notifications', ['$rootScope','growl', function ($rootScope, growl) {
        var notificationService = {};

        var addNotification = function (notificationsArray, notificationObj) {
            if (!angular.isObject(notificationObj)) {
                throw new Error('Only object can be added to the notification service');
            }
            ;
            notificationsArray.push(notificationObj);
            return notificationObj;
        };
        notificationService.current = [];

        notificationService.pushForCurrentRoute = function (notification) {
            growl.addErrorMessage(notification.msg);
            //return addNotification(notificationService.current, notification);
        };

        notificationService.growl = function(msg, type, ttl) {
            var cfg = {};
            if (ttl !== undefined ) {
                cfg.ttl = ttl;
            };
            var fnName = 'add'+type[0].toUpperCase()+type.slice(1)+'Message';
            var fn = growl[fnName];
            if (angular.isFunction(fn)) {
                fn(msg, cfg);
            } else {
                throw new Error('No notificaton type found: '+type);
            }

        }
        return notificationService;
    }])