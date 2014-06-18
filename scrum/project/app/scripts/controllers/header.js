angular.module('controllers.header',['security.service'])
    .controller('HeaderCtrl', ['$scope','$location', 'loginService', function($scope, $location, loginService) {
        $scope.location = $location;
        $scope.isAuthenticated = loginService.isAuthenticated;

        $scope.home = function() {
            $location.path('/')
        };

        $scope.isNavbarActive = function(navBarPath) {
            // TODO breadcrumbs.getFirst().name
            return false;
        }
    }]);