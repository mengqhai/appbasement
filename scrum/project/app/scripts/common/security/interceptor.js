angular.module('security.interceptor', ['security.retryQueue'])
// This http interceptor listens for authentication failures
    .factory('securityInterceptor', ['$injector', 'securityRetryQueue', function ($injector, queue) {
        return function (promise) {
            // Intercept only failed requests
            return promise.then(null, function (originResponse) {
                if (originResponse.status === 403) {
                    // The request bounced because it was not authorized - add a new request to the retry queue
                    promise = queue.pushRetryFn('You need to log in to perform the action.', function () {
                        // We must use $injector to get the $http service to prevent circular dependency
                        return $injector.get('$http')(originResponse.config);
                    });
                }
                return promise;
            });
        };
    }])
// We have to add the interceptor to the queue as a string because the interceptor depends upon service instances that are not available in the config block.
    .config(['$httpProvider', function ($httpProvider) {
        $httpProvider.responseInterceptors.push('securityInterceptor');
    }]);