angular.module('resources.utils', [])
    .factory('XeditableResourcePromise', ['$q', function ($q) {
        /*
         http://vitalets.github.io/angular-xeditable/#onbeforesave
         The main thing is that local model will be updated only if method returns true or undefined (or promise resolved to true/undefined). Commonly there are 3 cases depending on result type:
         - true or undefined: Success. Local model will be updated automatically and form will close.
         - false: Success. But local model will not be updated and form will close. Useful when you want to update local model manually (e.g. server changed values).
         - string: Error. Local model will not be updated, form will not close, string will be shown as error message. Useful for validation and processing errors.

         So the promise expected by xeditable is different from the one returned from the $resource.  We have to mediate that here.
         */
        return {
            xeditablePromise: function (resource) {
                var deferred = $q.defer();
                resource.$promise.then(function (response) {
                    deferred.resolve(true); // the value xeditable wanted
                }, function (responseError) {
                    deferred.reject(responseError.data.message);
                });
                return deferred.promise;
            }
        };
    }])