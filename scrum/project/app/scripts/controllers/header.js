angular.module('controllers.header',['security.service', 'services.breadcrumbs', 'services.notifications'])
    .controller('HeaderCtrl', ['$scope','$location', '$state','loginService','breadcrumbs','notifications',
        function($scope, $location, $state, loginService, breadcrumbs, notifications) {
            $scope.location = $location;
            $scope.isAuthenticated = loginService.isAuthenticated;
            $scope.breadcrumbs = breadcrumbs;

            $scope.home = function() {
                $location.path('/')
            };

            $scope.isNavbarActive = function(state) {
                console.info($state.current.name);
                return $state.current.name.indexOf(state) === 0;
            };

            $scope.notifications = notifications;

    }]);