angular.module('security.login.form', ['security.login.services', 'security.retryQueue', 'ui.bootstrap.modal', "ui.bootstrap.tpls"])
    .factory('loginDialog', ['$modal', 'securityRetryQueue', '$log', function ($modal, queue, $log) {
        // Login form dialog stuff
        var loginDialog = null;

        function openLoginDialog() {
            if (loginDialog) {
                $log.info('The dialog is already open.');
                return;
            }
            loginDialog = $modal.open({
                templateUrl: 'views/common/security/login/form.tpl.html',
                controller: 'LoginFormController',
                size: 'md'
            });
            loginDialog.result.then(function() {
                loginDialog = null;
                queue.retryAll();
            }, function() {
                loginDialog = null;
                queue.cancelAll();
            });
        }

        // Register a handler for when an item is added to the retry queue
        queue.onItemAddedCallbacks.push(function (retryItem) {
            if (queue.hasMore()) {
                service.showLogin();
            }
        });

        // The public API of the service
        var service = {
            // Show the modal login dialog
            showLogin: function () {
                openLoginDialog();
            },
            loginReason: function() {
                return queue.retryReason();
            }
        };
        return service;
    }])

// The LoginFormController provides the behaviour behind a reusable form to allow users to authenticate.
// This controller and its template (login/form.tpl.html) are used in a modal dialog box by the security service.
    .controller('LoginFormController', ['$scope', '$modalInstance', 'loginService', '$http', 'loginDialog', function ($scope, $modalInstance, loginService, $http, dailog) {
        // The model for this form
        $scope.user = {};

        // Any error message from failing to login
        $scope.authError = null;
        var instruction='Please enter your login details';
        $scope.authInfo = instruction;

        // The reason that we are being asked to login - for instance because we tried to access something to which we are not authorized
        // We could do something diffent for each reason here but to keep it simple...
        $scope.authReason = dailog.loginReason();

        // Attempt to authenticate the user specified in the form's model
        $scope.login = function () {
            // Clear any previous security errors
            $scope.authError = null;
            $scope.authInfo='Logging in...'

            // Try to login
            var result = loginService.login($scope.user.username, $scope.user.password).then(function (response) {
                if (!response.success) {
                    // If we get here then the login failed due to bad credentials
                    $scope.authError = response.failReason;
                    $scope.authInfo=instruction;
                } else {
                    $modalInstance.close(result);
                }
            }, function (x) {
                // If we get here then there was a problem with the login request to the server
                $scope.authError = x;
                $scope.authInfo=instruction;
            });
        }



        // Attempt to authenticate the user specified in the form's model
        $scope.clearForm = function () {
            $scope.user = {};
        };

        $scope.cancelLogin = function () {
            $modalInstance.dismiss("cancelLogin");
        };
    }]);
