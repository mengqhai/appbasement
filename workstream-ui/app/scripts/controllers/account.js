angular.module('controllers.account', ['resources.users', 'env'])
    .controller('AccountSettingsController', ['$scope', 'Users', 'envVars', function ($scope, Users, envVars) {
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
            Users.patch({userIdBase64: envVars.getCurrentUserIdBase64()}, patch).$promise.then(function () {
                angular.extend(currentUser, patch);
                envVars.setCurrentUser(currentUser);// update local storage
                $scope.message = 'User account updated.';
            });
        }
        $scope.canUpdate = function (ngFormController) {
            return !ngFormController.$dirty || ngFormController.$invalid;
        }
    }])
    .controller('AccountInfoController', ['$scope', 'Users', 'envVars', function ($scope, Users, envVars) {
        $scope.info = Users.getInfo({userIdBase64: envVars.getCurrentUserIdBase64()});
        $scope.instantType = 'QQ';
        $scope.socialType = 'Twitter';
        $scope.setInstantType = function (type) {
            $scope.instantType = type;
        }
        $scope.setSocialType = function (type) {
            $scope.socialType = type;
        }
        $scope.info.$promise.then(function () {
            for (var key in $scope.info) {
                if (key.indexOf('instant.') === 0) {
                    $scope.instant = $scope.info[key];
                    $scope.instantType = key.substring(8);
                } else if (key.indexOf('social.') === 0) {
                    $scope.social = $scope.info[key];
                    $scope.socialType = key.substring(7);
                }
            }
        });


    }]);