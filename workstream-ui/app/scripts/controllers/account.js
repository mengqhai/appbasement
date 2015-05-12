angular.module('controllers.account', ['resources.users', 'env'])
    .controller('AccountSettingsController', ['$scope', 'Users','envVars', function ($scope, Users,envVars) {
        var currentUser = $scope.getCurrentUser();
        $scope.user = {
            "firstName": currentUser.firstName,
            "email": currentUser.email
        };
        $scope.update = function (ngFormController) {
            ngFormController.firstName;
            var patch = {};
            if (ngFormController.firstName.$dirty) {
                patch.firstName = $scope.user.firstName;
            }
            if (ngFormController.email.$dirty) {
                patch.email = $scope.user.email;
            }
            console.log(patch);
            var userIdBase64 = btoa(currentUser.id);
            Users.patch({userIdBase64: userIdBase64}, patch).$promise.then(function() {
                angular.extend(currentUser, patch);
                envVars.setCurrentUser(currentUser);// update local storage
                $scope.message = 'User account updated.';
            });
        }
        $scope.canUpdate = function (ngFormController) {
            console.log(!ngFormController.$dirty && !ngFormController.$invalid);
            return !ngFormController.$dirty || ngFormController.$invalid;
        }
    }]);