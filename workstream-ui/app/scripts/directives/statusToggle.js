angular.module('directives.statusToggle', [])
    .directive('statusToggle', function() {
        var def = {
            restrict: 'E',
            scope: {
                status: '=',
                srefName: '@'
                // see http://stackoverflow.com/questions/14050195/what-is-the-difference-between-and-in-directive-scope
            },
            templateUrl: 'views/statusToggle.html'
        }
        return def;
    });