angular.module('controllers.login', ['ui.bootstrap', 'ui.bootstrap.modal', 'ui.bootstrap.tpls', 'services.security'])
    .controller('LoginFormController', ['$scope', 'loginService', '$modalInstance', function ($scope,loginService, $modalInstance) {
        $scope.user = {}; // model for the form
        $scope.authError = null; // error message
        var instruction = 'Please enter your user id and password';
        $scope.authInfo = instruction;

        $scope.login = function () {
            // Clear any previous security errors
            $scope.authError = null;
            $scope.authInfo = 'Logging in...'

            return loginService.login($scope.user.userId, $scope.user.password).then(function (response) {
                if (!response.data.success) {
                    $scope.authError = response.data.failReason;
                    $scope.authInfo = instruction;
                } else {
                    $scope.authInfo = "Login success";
                    $modalInstance && $modalInstance.dismiss("loginSuccess");
                }
            }, function (x) {
                // If we get here then there was a problem with the login request to the server
                $scope.authError = x;
                $scope.authInfo = instruction;
            })
        };

        // Attempt to authenticate the user specified in the form's model
        $scope.clearForm = function () {
            $scope.user = {};
        };

        $scope.cancelLogin = function () {
            $modalInstance && $modalInstance.dismiss("cancelLogin");
        };
    }])
    .controller('LoginDialogController', ['$modal', '$scope', function ($modal, $scope) {
        var dialog = null;
        $scope.open = function () {
            if (dialog) {
                // dialog already opened
                return;
            }
            ;
            dialog = $modal.open({
                templateUrl: 'views/security/loginForm.html',
                controller: 'LoginFormController',
                size: 'md'
            });

            dialog.result.then(function() {
                dialog = null;
            }, function() {
                dialog = null;
            });
        };
    }]);