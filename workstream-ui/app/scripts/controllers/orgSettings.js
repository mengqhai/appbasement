angular.module('controllers.orgSettings', [])
    .controller('OrgSettingsController', ['$scope', 'Orgs', function ($scope, Orgs) {
        $scope.myAdminOrgs = Orgs.getMyAdminOrgs();
    }])
    .controller('OrgSettingsGeneralController', ['$scope', 'Orgs', '$stateParams', function ($scope, Orgs, $stateParams) {
        $scope.org = Orgs.get({orgId: $stateParams.orgId});
        $scope.canUpdate = function (ngFormController) {
            return !ngFormController.$invalid && ngFormController.$dirty;
        }

        $scope.update = function (ngFormController) {
            var patch = {};
            for (var key in $scope.org) {
                if (key.indexOf('$') !== 0 && ngFormController[key]
                    && ngFormController[key].$dirty) {
                    patch[key] = $scope.org[key];
                }
            }
            Orgs.patch({orgId: $stateParams.orgId}, patch).$promise.then(function () {
                $scope.message = 'Organization updated.'
            })
        }

    }]);