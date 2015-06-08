angular.module('controllers.orgs', ['resources.orgs'])
    .controller('OrgAddFormController', ['$scope', 'Orgs', '$rootScope', '$modalInstance', '$timeout',
        function ($scope, Orgs, $rootScope, $modalInstance, $timeout) {
            $scope.addBy = 'create';
            function reset() {
                $scope.foundOrg = null;
                $scope.error = null;
                $scope.message = null;
            }

            /** for creating **/
            $scope.org = {};
            $scope.identifier = {};
            $scope.clearForm = function () {
                $scope.org = {};
                $scope.identifier = {};
            }
            $scope.cancel = function() {
                $modalInstance.close();
            }
            $scope.canCreate = function (ngFormController) {
                return $scope.addBy === 'create' && ngFormController.$dirty && ngFormController.$valid;
            }
            $scope.createOrg = function (ngFormController) {
                reset();
                Orgs.create($scope.org, function (org) {
                    if ($modalInstance) {
                        $rootScope.$broadcast('orgs.create', org);
                        $modalInstance.close();
                    }
                }, function (error) {
                    error.data && ($scope.error = error.data.message);
                })
            }

            /** for joining **/
            $scope.foundOrg = null;
            $scope.canJoin = function (ngFormController) {
                return $scope.foundOrg;
            }
            $scope.canSearch = function (ngFormController) {
                return ngFormController.searchIdentifier.$dirty && ngFormController.searchIdentifier.$valid;
            }
            $scope.$watch('identifier.v', function (newValue, oldValue) {
                $scope.foundOrg && ($scope.foundOrg = null);
            })
            $scope.search = function (ngFormController) {
                reset();
                Orgs.findByIdentifier({identifier: $scope.identifier.v}, function (org) {
                    $scope.foundOrg = org;
                }, function (error) {
                    error.data && ($scope.error = error.data.message);
                })
            }
            $scope.join = function (ngFormController) {
                Orgs.join({orgId: $scope.foundOrg.id}, null, function (success) {
                    $scope.message = 'Request submitted and waiting for approval of the administrators in ' + $scope.foundOrg.name;
                    $timeout(function () {
                        $modalInstance && $modalInstance.close();
                    }, 2000);
                }, function (error) {
                    error.data && ($scope.error = error.data.message);
                })
            }
        }])