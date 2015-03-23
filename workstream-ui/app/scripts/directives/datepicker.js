angular.module('directives.datepicker', ['ui.bootstrap'])
    .directive('wsDatepicker', function () {
        return {
            restrict: 'E',
            templateUrl: 'views/datepicker.html',
            scope: {
                onSelected: '&',  // must return a promise
                dateValue: '='
            },
            link: function (scope, element, attrs) {
                scope.nullLabel = attrs['nullLabel'];
                if (!scope.nullLabel) {
                    scope.nullLabel = 'Not set';
                }
                scope.opened = false;
                scope.open = function ($event) {
                    $event.preventDefault();
                    $event.stopPropagation();
                    scope.dateInternal = scope.dateValue;
                    scope.opened = true;
                };
                scope.$watch('dateInternal', function (newValue, oldValue) {
                    if (newValue == oldValue) {
                        return;
                    }
                    ;
                    if (scope.dateInternal == scope.dateValue) {
                        return;
                    }
                    if (attrs['onSelected']) {
                        scope.onSelected({newValue: newValue, oldValue: oldValue}).then(function (sucess) {
                            scope.dateValue = newValue;
                        }, function (error) {
                            scope.dueDateError = error;
                        })
                    } else {
                        scope.dateValue = newValue;
                    }
                    ;
                })
            }
        };
    });