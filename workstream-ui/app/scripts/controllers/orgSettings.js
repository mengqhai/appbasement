angular.module('controllers.orgSettings', ['controllers.groups'])
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
                $scope.message = 'Organization updated.';
                ngFormController.$setPristine();
            })
        }

    }])
    .controller('OrgSettingsMembersController', ['$scope', 'Orgs', 'Groups', 'Users', '$stateParams', '$modal',
        function ($scope, Orgs, Groups, Users, $stateParams, $modal) {
            $scope.groups = Orgs.getGroupsInOrg({orgId: $stateParams.orgId, withDetails: true});
            $scope.groupMembers = {};
            $scope.groups.$promise.then(function () {
                for (var i = 0; i < $scope.groups.length; i++) {
                    $scope.groupMembers[$scope.groups[i].groupId] = Groups.getMembers({groupId: $scope.groups[i].groupId});
                }
            });
            $scope.getUserPicUrl = Users.getUserPicUrl;
            var groupDialog = null;
            $scope.openGroupDialog = function (group) {
                groupDialog = $modal.open({
                    templateUrl: 'views/groupForm.html',
                    controller: 'GroupFormController',
                    resolve: {
                        group: function () {
                            return group;
                        }
                    }
                });
            };
            $scope.closeDialog = function () {
                if (groupDialog) {
                    groupDialog.dismiss('closeGroupDialog');
                }
            };

            var getAddToList = function (user) {
                var myGroups = []; //angular.extend([], $scope.groups);
                for (var i = 0; i < $scope.groups; i++) {
                    var group = $scope.groups[i];
                    var groupId = group.groupId;
                    var members = $scope.groupMembers[groupId];
                    if (members.indexOf(user) !== -1) {
                        myGroups.add(group);
                    }
                }
                console.log(myGroups);
                return myGroups;
            };
            $scope.userMenuToggled = function(open, user) {
                if (open) {
                    $scope.myAddToList = getAddToList(user);
                }
            };
        }]);