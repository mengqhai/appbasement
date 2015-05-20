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
            return (ngFormController.$dirty && !ngFormController.$invalid);
        }
    }])
    .controller('AccountInfoController', ['$scope', 'Users', 'envVars', function ($scope, Users, envVars) {
        $scope.info = Users.getInfo({userIdBase64: envVars.getCurrentUserIdBase64()});
        $scope.instantType = 'QQ';
        $scope.socialType = 'Twitter';
        var subTypeChange = false;
        $scope.setMainSubType = function (mainType, subType) {
            var oldType = $scope[mainType + 'Type'];
            $scope[mainType + 'Type'] = subType;
            $scope.info[mainType + '.' + $scope[mainType + 'Type']] = $scope[mainType];
            $scope.info[mainType + '.' + oldType] = null; // make sure the old value-key can be deleted
            subTypeChange = true;
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

        $scope.canUpdate = function (ngFormController) {
            return ngFormController.$dirty || subTypeChange;
        }


        $scope.update = function (ngFormController) {
            var patch = {};
            for (var key in $scope.info) {
                if (key.indexOf('$') !== 0 && ngFormController[key]
                    && ngFormController[key].$dirty) {
                    patch[key] = $scope.info[key];
                } else if (key.indexOf('instant.') === 0) {
                    patch[key] = $scope.info[key];
                } else if (key.indexOf('social.') === 0) {
                    patch[key] = $scope.info[key];
                }
            }

            // for newly set instant/social values
            if (ngFormController.instant.$dirty) {
                var key = 'instant.' + $scope.instantType;
                patch[key] = $scope.instant;
            }
            if (ngFormController.social.$dirty) {
                var key = 'social.' + $scope.socialType;
                patch[key] = $scope.social;
            }
            //console.log(patch);
            Users.setInfo({userIdBase64: envVars.getCurrentUserIdBase64()}, patch).$promise.then(function () {
                $scope.message = 'Profile information updated.';
            });
        }
    }])
    .controller('AccountPicController', ['$scope', 'Users', 'envVars', '$rootScope', function ($scope, Users, envVars, $rootScope) {
        $scope.currentUserId = envVars.getCurrentUserId();
        $scope.canUpload = function (ngFormController) {
            //console.log(!ngFormController.$invalid && ngFormController.$dirty);
            // http://stackoverflow.com/questions/16207202/required-attribute-not-working-with-file-input-in-angular-js
            return $scope.picFile !== undefined;
        }
        $scope.uploadFile = function () {
            Users.updatePic($scope.currentUserId, $scope.picFile).success(function () {
                $rootScope.$emit('user.pic.update');
            });
        }
    }])
    .controller('AccountPasswordController', ['$scope', function ($scope) {
        $scope.canUpdate = function(ngFormController) {
            if (!$scope.password || !$scope.confirmation) {
                return false;
            }
            var eq = $scope.password == $scope.confirmation;
            if (!eq) {
                $scope.message = 'Password does not match';
            } else {
                $scope.message = '';
            }
            return eq;
        }
    }]);