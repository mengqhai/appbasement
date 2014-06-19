angular.module('controllers.header',['security.service', 'services.breadcrumbs', 'services.notifications'])
    .controller('HeaderCtrl', ['$scope','$location', 'loginService','breadcrumbs','notifications',
        function($scope, $location, loginService, breadcrumbs, notifications) {
            $scope.location = $location;
            $scope.isAuthenticated = loginService.isAuthenticated;
            $scope.breadcrumbs = breadcrumbs;

            $scope.home = function() {
                $location.path('/')
            };

            $scope.isNavbarActive = function(navBarPath) {
                return navBarPath === breadcrumbs.getLast().name;
            };

            $scope.notifications = notifications;

    }]);