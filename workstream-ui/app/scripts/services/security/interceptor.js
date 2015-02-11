'use strict';

angular.module('service.security.interceptor', [])
    .factory('SecurityInterceptor', ['SecurityRetryQueue', 'envVars', function (queue, envVars) {
        return {
            responseError: function (originResponse) {
                if (originResponse.status === 401) {
                    // The request bounced because it was not authorized - add a new request to the retry queue
                    queue.pushRetryFn('You need to log in to perform the action.', function () {
                        // We must use $injector to get the $http service to prevent circular dependency
                        return $injector.get('$http')(originResponse.config);
                    });
                }
                return originResponse;
            }//,
//            request: function(config) {
//                config.headers['x-api-key']=envVars.apiKey;
//                return config;
//            }
        };
    }])
    .config(['$httpProvider', function ($httpProvider) {
        $httpProvider.interceptors.push('SecurityInterceptor');
    }])
    .factory('SecurityRetryQueue', ['$q', '$log', function ($q, $log) {
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
;