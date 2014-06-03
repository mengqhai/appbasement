angular.module('security.login.toolbar', ['security.login.services', 'security.login.form'])
    .directive('loginToolbar', ['loginService', 'loginDialog', function (loginService, loginDialog) {
        var directive = {
            templateUrl: 'views/common/security/login/toolbar.tpl.html',
            restrict: 'E',
            replace: true,
            scope: true,
            link: function ($scope, $element, $attrs, $controller) {
                $scope.isAuthenticated = loginService.isAuthenticated;
                $scope.login = loginDialog.showLogin;
                $scope.logout = loginService.logout;
                $scope.$watch(function () {
                    return loginService.currentUser;
                }, function (currentUser) {
                    $scope.currentUser = currentUser;
                })
            }
        };
        return directive;
    }]);