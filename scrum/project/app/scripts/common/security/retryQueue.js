angular.module('security.retryQueue', [])
// This is a generic retry queue for security failures.  Each item is expected to expose two functions: retry and cancel.
    .factory('securityRetryQueue', ['$q', '$log', function ($q, $log) {
        var retryQueue = [];
        var service = {
            hasMore: function () {
                return retryQueue.length > 0;
            },
            // The security service puts its own handler in here!
            onItemAddedCallbacks: [],
            push: function (retryItem) {
                retryQueue.push(retryItem);
                // Call all the onItemAdded callbacks
                angular.forEach(service.onItemAddedCallbacks, function (cb) {
                    try {
                        cb(retryItem);
                    } catch (e) {
                        $log.error('securityRetryQueue.push(retryItem): callback threw an error' + e);
                    }
                });
            },
            // Core function
            pushRetryFn: function (reason, retryFn) {
                // The reason parameter is optional
                if (arguments.length === 1) {
                    retryFn = reason;
                    reason = undefined;
                }

                // The deferred object that will be resolved or rejected by calling retry or cancel
                var deferred = $q.defer();
                var retryItem = {
                    reason: reason,
                    retry: function () {
                        // Wrap the result of retryFn into a promise if it is not already
                        $q.when(retryFn()).then(function (value) {
                            // If it was successful then resolve our defered
                            deferred.resolve(value);
                        }, function (value) {
                            // Otherwise reject it
                            deferred.reject(value);
                        })
                    },
                    cancel: function () {
                        deferred.reject();
                    }
                };
                service.push(retryItem);
                return deferred.promise;
            },
            retryReason: function () {
                return service.hasMore() && retryQueue[0].reason;
            },
            cancelAll: function () {
                while (service.hasMore()) {
                    retryQueue.shift().cancel();
                }
            },
            retryAll: function () {
                while (service.hasMore()) {
                    retryQueue.shift().retry();
                }
            }
        };
        return service;
    }]);