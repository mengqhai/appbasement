/**
 * Created with JetBrains WebStorm.
 * User: liuli
 * Date: 14-3-19
 * Time: 下午8:55
 * To change this template use File | Settings | File Templates.
 */
angular.module('resource.users', ['ngResource'])
    .factory('Users', function ($resource) {
        var Users = $resource('http://localhost:8080/angularJsPlay/user/:id', {
            id:'@itDoesNotMatterWhatIsFollowed'
        });
        return Users;
    })
    .controller('UsersCtrl', function ($scope, $log, Users) {
        $scope.users = Users.query({}, function (users) {
            $log.info($scope.users.length);
        });
    });