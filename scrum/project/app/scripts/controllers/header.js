angular.module('controllers.header',['security.service', 'services.breadcrumbs'])
    .controller('HeaderCtrl', ['$scope','$location', 'loginService','breadcrumbs', function($scope, $location, loginService, breadcrumbs) {
        $scope.location = $location;
        $scope.isAuthenticated = loginService.isAuthenticated;
        $scope.breadcrumbs = breadcrumbs;

        $scope.home = function() {
            $location.path('/')
        };

        $scope.isNavbarActive = function(navBarPath) {
            return navBarPath === breadcrumbs.getFirst().name;
        }
    }]);