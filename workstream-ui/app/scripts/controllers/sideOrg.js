angular.module('controllers.sideOrg', ['env', 'resources.orgs'])
    .controller('SideOrgController', ['$scope', 'Orgs', 'envVars', function ($scope, Orgs, envVars) {
        $scope.myOrgs = [];
        $scope.orgProjects = {};
        $scope.orgUsers = {};


        $scope.loadMyOrgs = function () {
            if (!envVars.isLoggedIn()) {
                return;
            }
            $scope.myOrgs = Orgs.getMyOrgs();
        }

        $scope.loadProjectsForOrg = function (org) {
            $scope.orgProjects[org.id] = Orgs.getProjectsInOrg({orgId: org.id});
        };
        $scope.loadUsersInOrg = function (org) {
            $scope.orgUsers[org.id] = Orgs.getUsersInOrg({orgId: org.id});
        }
    }]);