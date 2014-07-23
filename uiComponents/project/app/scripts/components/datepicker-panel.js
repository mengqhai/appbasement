angular.module('components.datepicker-panel', ['ui.bootstrap.datepicker'])
    .directive('datepickerPanel', function() {
        return {
            restrict: 'E',
            replace: true,
            templateUrl:'/views/components/datepicker-panel.tpl.html',
            scope: {
                dateInfo: '='
            },
            link: function(scope, element, attrs) {
                scope.date = scope.dateInfo.date || new Date();

                scope.save = function() {
                    scope.dateInfo.date = scope.date;
                }

                scope.makeUrgent = function() {
                    scope.dateInfo.urgent = true;
                }

                scope.makeNoDueDate = function() {
                    scope.dateInfo.date = null;
                }
            }
        };
    });