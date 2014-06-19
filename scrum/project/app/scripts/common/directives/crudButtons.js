angular.module('directives.crud.buttons', [])
    .directive('crudButtons', function() {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: 'views/common/directives/crudButtons.html'
        }
    });