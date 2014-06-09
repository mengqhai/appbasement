angular.module('uniqueChecks', ['resources.users'])
    .directive('uniqueUsername', ['Users', '$timeout', function (Users, $timeout) {
        return {
            require: 'ngModel',
            restrict: 'A',
            link: function (scope, el, attrs, ctrl) {
                // We need to check that the value is different to the original
                var original;
                ctrl.$formatters.unshift(function (modelValue) {
                    original = modelValue;
                    return modelValue;
                });


                // if we type too fast, we will be doing a lot of requests to the server (one for every new character typed).
                // To avoid this unnecessary load, we will be using the $timeout function provided by AngularJS. When a
                // name is typed, we will wait 500 ms to make the call to the backend. If we type again before the
                // timeout is fired, we will cancel the previous call, and make a new one.
                var stop_timeout;

                // We are only checking with the server in the $parser, that is, when the user changes
                // the input. If the value is updated programmatically, via the model, we assume that
                // the application business logic ensures that this is a valid e-mail address.
                //using push() here to run it as the last parser, after we are sure that other validators were run
                ctrl.$parsers.push(function (viewValue) {
                    if (stop_timeout) {
                        $timeout.cancel(stop_timeout); // cancel last timeout
                        // This means that we won't actually issue any request unless user stopped
                        // changing the value for more then 500 ms.  Which prevents sending too much
                        // request while user is typing.
                    }
                    ;

                    if (!viewValue || viewValue === original) {
                        // we don't validate an empty string
                        ctrl.$setValidity('uniqueUseranme', true);
                        return viewValue;
                    }
                    ;


                    stop_timeout = $timeout(function () {
                        Users.isUsernameUnique(viewValue, function (result) {
                            if (result.value) {
                                ctrl.$setValidity('uniqueUseranme', true);
                            } else {
                                ctrl.$setValidity('uniqueUseranme', false);
                            }
                        });
                    }, 500);
                    return viewValue;
                });
            }
        };
    }]);