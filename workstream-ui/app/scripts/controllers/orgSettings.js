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
            var initUngroupedUsers = function() {
                $scope.ungroupedUsers = Orgs.getUsersInOrg({orgId: $stateParams.orgId});
            };
            initUngroupedUsers();
            var updateUngroupedUsers = function () {
                for (var i = 0; i < $scope.groups.length; i++) {
                    var theMembers = Groups.getMembers({groupId: $scope.groups[i].groupId});
                    $scope.groupMembers[$scope.groups[i].groupId] = theMembers;
                    theMembers.$promise.then(function (arr) {
                        for (var i = 0; i < arr.length; i++) {
                            var matchIdx = -1;
                            for (var j = 0; j < $scope.ungroupedUsers.length; j++) {
                                if (arr[i].id === $scope.ungroupedUsers[j].id) {
                                    // this is a grouped user, need to be removed
                                    matchIdx = j;
                                    break;
                                }
                            }
                            if (matchIdx !== -1) {
                                $scope.ungroupedUsers.splice(matchIdx, 1);
                            }
                        }
                    })
                }
            }
            $scope.groups.$promise.then(function () {
                updateUngroupedUsers();
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
                var availableGroups = []; //angular.extend([], $scope.groups);
                for (var i = 0; i < $scope.groups.length; i++) {
                    var g = $scope.groups[i];
                    var groupId = g.groupId;
                    var members = $scope.groupMembers[groupId];
                    var isMember = false;
                    for (var j = 0; j < members.length; j++) {
                        var m = members[j];
                        if (m.id === user.id) {
                            isMember = true;
                            break;
                        }
                    }
                    if (!isMember) {
                        availableGroups.push(g);
                    }

                }
                return availableGroups;
            };
            $scope.userMenuToggled = function (open, user) {
                if (open) {
                    $scope.myAddToList = getAddToList(user);
                }
            };

            $scope.deleteGroupMember = function (group, user) {
                Groups.deleteMember({groupId: group.groupId, userIdBase64: btoa(user.id)}).$promise.then(function () {
                    console.log('deleted user ' + user.id + ' from group ' + group.groupId);
//                    var members = $scope.groupMembers[group.groupId];
//                    var idx = members.indexOf(user);
//                    members.splice(idx, 1);
                    initUngroupedUsers();
                    updateUngroupedUsers();
                });
            };
            $scope.addGroupMember = function (group, user) {
                Groups.addMember({groupId: group.groupId, userIdBase64: btoa(user.id)}, null).$promise.then(function () {
                    console.log('added user ' + user.id + ' to group ' + group.groupId);
                    updateUngroupedUsers();
                })
            }

        }]);