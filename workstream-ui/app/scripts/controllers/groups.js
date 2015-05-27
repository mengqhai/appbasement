angular.module('controllers.groups', ['resources.groups'])
    .controller('GroupFormController', ['$scope', '$modalInstance', 'Groups', 'group', function($scope, $modalInstance, Groups, group) {
        $scope.group = angular.copy(group, {});
        $scope.canUpdate = function (ngFormController) {
            return !ngFormController.$invalid && ngFormController.$dirty;
        };
        $scope.update = function(ngFormController) {
            var patch = {};
            for (var key in $scope.group) {
                if (key.indexOf('$') !== 0 && ngFormController[key]
                    && ngFormController[key].$dirty) {
                    patch[key] = $scope.group[key];
                }
            }
            Groups.patch({groupId:group.groupId}, patch).$promise.then(function() {
                console.log('Group updated.');
                angular.extend(group, patch);
                $scope.message = 'Group updated';
                $modalInstance.close();
            })
        }
    }]);