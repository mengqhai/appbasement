angular.module('controllers.sideOrg', ['env', 'resources.orgs'])
    .controller('SideOrgController', ['$scope', 'Orgs', 'envVars', function ($scope, Orgs, envVars) {
        $scope.myOrgs = [];
        $scope.orgProjects = {};


        $scope.loadMyOrgs = function () {
            if (!envVars.isLoggedIn()) {
                return;
            }
            $scope.myOrgs = Orgs.getMyOrgs();
        }

        $scope.loadProjectsForOrg = function (org) {
            $scope.orgProjects[org.id] = Orgs.getProjectsInOrg({orgId: org.id});
        }
    }]);