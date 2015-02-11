'use strict';

angular.module('controllers.login', ['ui.bootstrap', 'ui.bootstrap.modal', 'ui.bootstrap.tpls', 'services.security', 'service.security.interceptor'])
    .controller('LoginFormController', ['$scope', 'loginService', '$modalInstance', 'SecurityRetryQueue', function ($scope, loginService, $modalInstance, queue) {
        $scope.user = {}; // model for the form
        $scope.authError = null; // error message
        var instruction = 'Please enter your user id and password';
        $scope.authInfo = instruction;

        // The reason that we are being asked to login - for instance because we tried to access something to which we are not authorized
        // We could do something diffent for each reason here but to keep it simple...
        $scope.authReason = queue.retryReason();

        $scope.login = function () {
            // Clear any previous security errors
            $scope.authError = null;
            $scope.authInfo = 'Logging in...';

            return loginService.login($scope.user.userId, $scope.user.password).then(function (response) {
                if (!response.data.success) {
                    $scope.authError = response.data.failReason;
                    $scope.authInfo = instruction;
                } else {
                    $scope.authInfo = 'Login success';
                    if ($modalInstance) {
                        $modalInstance.close(response.data);
                    }
                }
            }, function (x) {
                // If we get here then there was a problem with the login request to the server
                $scope.authError = x;
                $scope.authInfo = instruction;
            });
        };

        // Attempt to authenticate the user specified in the form's model
        $scope.clearForm = function () {
            $scope.user = {};
        };

        $scope.cancelLogin = function () {
            if ($modalInstance) {
                $modalInstance.dismiss('cancelLogin');
            }
        };
    }])
    .controller('LoginDialogController', ['$modal', '$scope', 'SecurityRetryQueue', function ($modal, $scope, queue) {
        var dialog = null;
        $scope.open = function () {
            if (dialog) {
                // dialog already opened
                return;
            }
            dialog = $modal.open({
                templateUrl: 'views/security/loginForm.html',
                controller: 'LoginFormController',
                size: 'md'
            });

            dialog.result.then(function () {
                dialog = null;
                queue.retryAll();
            }, function () {
                dialog = null;
                queue.cancelAll();
            });
        };

        queue.onItemAddedCallbacks.push(function (retryItem) {
            if (queue.hasMore()) {
                $scope.open();
            }
        });
    }]);