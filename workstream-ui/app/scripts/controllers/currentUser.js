angular.module('controllers.currentUser', ['env', 'resources.users'])
    .controller('CurrentUserController', ['envVars', '$scope', 'Users', '$modal', function (envVars, $scope, Users, $modal) {
//        $scope.isLoggedIn = function () {
//            return envVars.isLoggedIn();
//        };
        $scope.getCurrentUser = function () {
            return envVars.getCurrentUser();
        };
        var dialog = null;
        var orgCfg = {
            templateUrl: 'views/orgAddForm.html',
            controller: 'OrgAddFormController',
            scope: $scope
        }
        $scope.openOrgAddDialog = function() {
            dialog = $modal.open(orgCfg);
        }
    }]);