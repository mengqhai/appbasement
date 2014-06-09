angular.module('alert', [])
    // a alert directive using transclusion
    // usage:
    // <alert ng-repeat="alert in alerts" type="alert.type" close="closeAlert($index)">
    // </alert>
    .directive('alert', function () {
        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            template: '<div class="alert alert-{{type}}">' +
                '<button type="button" class="close" ng-click="close()">' +
                '&times;' +
                '</button>' +
                '<div ng-transclude></div> ' +
                '</div>',
            scope: {type: '=', close: '&'}
        }
    });
angular.module('alert')
    // usage:
    // <alert-list alerts="alertsInScope"></alert-list>
    .directive('alertList',function () {
        return {
            restrict: 'E',
            replace: true,
            template: '<alert ng-repeat="alert in alerts" type="alert.type" close="closeAlert($index)">' +
                '{{alert.msg}}' +
                '</alert>',
            scope: {
                alerts: '='
            },
            link: function(scope, ele, attrs) {
                scope.closeAlert= function(index) {
                    scope.alerts.splice(index, 1);;
                }
            }
        }
    });