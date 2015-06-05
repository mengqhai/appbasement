'use strict';

angular.module('controllers.login', ['ui.bootstrap', 'ui.bootstrap.modal', 'ui.bootstrap.tpls', 'services.security',
        'resources.users'])
    .controller('LoginFormController', ['$scope', 'loginService', '$modalInstance', function ($scope, loginService, $modalInstance) {
        $scope.user = {}; // model for the form
        $scope.authError = null; // error message
        var instruction = 'Please enter your user id and password';
        $scope.authInfo = instruction;

        // The reason that we are being asked to login - for instance because we tried to access something to which we are not authorized
        // We could do something diffent for each reason here but to keep it simple...
        //$scope.authReason = null;

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
    .controller('LoginDialogController', ['$modal', '$scope', 'loginService', 'authService', 'envVars', '$timeout',
        function ($modal, $scope, loginService, authService, envVars, $timeout) {
            var dialog = null;
            $scope.open = function () {
                if (dialog) {
                    // dialog already opened
                    return;
                }
                dialog = $modal.open({
                    templateUrl: 'views/security/loginForm.html',
                    controller: 'LoginFormController',
                    size: 'md',
                    scope: $scope
                });

                dialog.result.then(function (data) {
                    dialog = null;
                    //queue.retryAll();
                    // see https://github.com/witoldsz/angular-http-auth
                    authService.loginConfirmed(data, function (config) {
                        config.params.api_key = data.apiToken;
                        return config;
                    });
                    $scope.$emit('login');
                    $scope.authReason = null;
                }, function () {
                    dialog = null;
                    //queue.cancelAll();
                    $scope.authReason = null;
                });
            };

            $scope.$on('event:auth-loginRequired', function (event) {
                $scope.authReason = 'You need to log in to perform the action.';
                $scope.open();
            });

//        queue.onItemAddedCallbacks.push(function (retryItem) {
//            if (queue.hasMore()) {
//                $scope.open();
//            }
//        });

            $scope.logout = function () {
                loginService.logout();
                $scope.$emit('logout');
            };


            /** For Sign up button **/
            $scope.openSignUpDialog = function () {
                $modal.open({
                    templateUrl: 'views/security/signUpForm.html',
                    controller: 'SignUpFormController',
                    size: 'sm',
                    scope: $scope
                });
            }
        }])
    .controller('SignUpFormController', ['$scope', 'Users', '$modalInstance',
        function ($scope, Users, $modalInstance) {
            $scope.hash = Math.random();
            $scope.updateCaptcha = function() {
                $scope.hash = Math.random();
            };
            $scope.tempKey = Math.random();
            $scope.user = {};
            $scope.clearForm = function () {
                $scope.user = {};
            };
            $scope.cancelSignUp = function () {
                $modalInstance && $modalInstance.close();
            }
            $scope.canSignUp = function (ngFormController) {
                var eq = ($scope.confirmation === $scope.user.password);
                return (ngFormController.$dirty && !ngFormController.$invalid && eq);
            }
            $scope.signUp = function (ngFormController) {
                Users.create({captcha: $scope.captcha}, $scope.user, function(user) {
                    console.log(user);
                    $modalInstance.close();
                }, function(response) {
                    if (response.data) {
                        $scope.error = response.data.message;
                    }
                    console.log(response.data);
                })
            }
        }]);