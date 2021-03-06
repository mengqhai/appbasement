'use strict';

// deprecated
// replaced by https://github.com/witoldsz/angular-http-auth

angular.module('service.security.interceptor', [])
    .factory('SecurityInterceptor', ['SecurityRetryQueue', 'envVars', '$injector', '$q', function (queue, envVars, $injector, $q) {
        return {
            responseError: function (originResponse) {
                if (originResponse.status === 401) {
                    // The request bounced because it was not authorized - add a new request to the retry queue
                    queue.pushRetryFn('You need to log in to perform the action.', function () {
                        if (envVars.getApiKey() !== originResponse.config.params.api_key) {
                            originResponse.config.params.api_key = envVars.getApiKey();
                        }

                        // We must use $injector to get the $http service to prevent circular dependency
                        var $http = $injector.get('$http');
                        var newPromise = $http(originResponse.config);
                        return newPromise;
                    });
                }
                return $q.reject(originResponse);
                // here must $q.reject() otherwise the promise will only invoke the success callback
                // http://bneijt.nl/blog/post/angularjs-intercept-api-error-responses/
            }//,
//            request: function(config) {
//                config.headers['x-api-key']=envVars.apiKey;
//                return config;
//            }
        };
    }])
    .config(['$httpProvider', function ($httpProvider) {
        //$httpProvider.interceptors.push('SecurityInterceptor');
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