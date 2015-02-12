angular.module('controllers.currentUser', ['env', 'resources.users'])
    .controller('CurrentUserController', ['envVars', '$scope', 'Users', function (envVars, $scope, Users) {
//        $scope.isLoggedIn = function () {
//            return envVars.isLoggedIn();
//        };
        $scope.getCurrentUser = function () {
            return envVars.getCurrentUser();
        };
        $scope.getCurrentUserPicUrl = function() {
            return Users.getUserPicUrl(envVars.getCurrentUser().id);
        }
    }]);