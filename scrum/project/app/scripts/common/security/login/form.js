angular.module('security.login.form', ['security.login.services', 'ui.bootstrap.modal', "ui.bootstrap.tpls"])
    .factory('loginDialog', ['$modal', function ($modal) {
        // Login form dialog stuff
        var loginDialog = null;

        function openLoginDialog() {
            if (loginDialog) {
                throw new Error('Trying to open a dialog that is already open!');
            }
            loginDialog = $modal.open({
                templateUrl: 'views/common/security/login/form.tpl.html',
                controller: 'LoginFormController'
            });
            loginDialog.result.then(onLoginDialogClose, onLoginDialogClose);
        }

        function onLoginDialogClose(success) {
            loginDialog = null;
            if (success) {
                console.log(success);
            }
        }

        // The public API of the service
        var service = {
            // Show the modal login dialog
            showLogin: function () {
                openLoginDialog();
            }};
        return service;
    }])

// The LoginFormController provides the behaviour behind a reusable form to allow users to authenticate.
// This controller and its template (login/form.tpl.html) are used in a modal dialog box by the security service.
    .controller('LoginFormController', ['$scope', '$modalInstance', 'loginService', function ($scope, $modalInstance, loginService) {
        // The model for this form
        $scope.user = {};

        // Any error message from failing to login
        $scope.authError = null;

        // The reason that we are being asked to login - for instance because we tried to access something to which we are not authorized
        // We could do something diffent for each reason here but to keep it simple...
        $scope.authReason = null;
        if (loginService.getLoginReason()) {
            $scope.authReason = loginService.getLoginReason();
        }

        // Attempt to authenticate the user specified in the form's model
        $scope.login = function () {
            // Clear any previous security errors
            $scope.authError = null;

            // Try to login
            var result = loginService.login($scope.user.username, $scope.user.password).then(function (response) {
                if (!response.success) {
                    // If we get here then the login failed due to bad credentials
                    $scope.authError = response.failReason;
                } else {
                    $modalInstance.close(result);
                }
            }, function (x) {
                // If we get here then there was a problem with the login request to the server
                $scope.authError = x;
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