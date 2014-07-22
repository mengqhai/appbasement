angular.module('components.nice-checkbox', [])
    .directive('niceCheckbox',function() {
        var def = {
            restrict: 'A',
            replace: true,
            transclude: 'element',
            templateUrl: '/views/components/nice-checkbox.tpl.html',
            link: function(scope, ele, attrs) {

            }
        };
        return def;
    });