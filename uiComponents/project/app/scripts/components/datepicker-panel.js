angular.module('components.datepicker-panel', ['ui.bootstrap.datepicker', 'ui.bootstrap.buttons'])
    .directive('datepickerPanel', function($log) {
        return {
            restrict: 'E',
            replace: true,
            templateUrl:'/views/components/datepicker-panel.tpl.html',
            scope: {
                dateInfo: '=',
                commit: '=' // this method must return a promise
            },
            link: function(scope, element, attrs) {
                scope.date = scope.dateInfo.date || new Date();
                scope.urgent = scope.dateInfo.urgent;

                var updateDateInfo = function() {
                    scope.dateInfo.date = scope.date;
                    scope.dateInfo.urgent = scope.urgent;
                }

                scope.save = function() {
                    if (scope.commit) {
                        var promise = scope.commit(scope.date);
                        if (promise) {
                            promise.then(updateDateInfo, function(msg) {
                                $log.error("datepicker failed to commit:"+msg);
                            });
                        }
                    } else {
                        updateDateInfo();
                    }
                }

                scope.toggleUrgent = function() {
                    scope.save();
                }

                scope.makeNoDueDate = function() {
                    scope.date = null;
                    scope.save();
                }
            }
        };
    });