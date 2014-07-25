angular.module('components.datepicker-panel', ['ui.bootstrap.datepicker', 'ui.bootstrap.buttons',
    'ngAnimate'])
    .directive('datepickerPanel', function($log, $filter) {
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
                    updateDateType();
                }

                scope.save = function() {
                    if (scope.commit) {
                        var promise = scope.commit(scope.date);
                        if (promise) {
                            promise.then(updateDateInfo, function(msg) {
                                $log.error("datepicker failed to commit:"+msg);
                                updateDateType(); // roll back the radio
                            });
                        }
                    } else {
                        updateDateInfo();
                    }
                }

                var updateDateType = function () {
                    // variable to toggle the Urgent/No Due Date button
                    scope.dateType = $filter('dateInfo')(scope.dateInfo);
                }

                updateDateType();
                scope.makeUrgent = function() {
                    scope.urgent = true;
                    scope.save();
                }

                scope.makeNoDueDate = function() {
                    scope.date = null;
                    scope.urgent = false;
                    scope.save();
                }

                scope.pickDate = function() {
                    scope.urgent = false;
                    scope.save();
                }

                scope.disablePick = function () {
                    var d1 = scope.date;
                    var d2 = scope.dateInfo.date;
                    return !((d1.getDate() - d2.getDate()) ||
                        (d1.getMonth() - d2.getMonth()) ||
                        (d1.getFullYear() - d2.getFullYear()));
                }
            }
        };
    })
    .directive('datepickerBtn', function() {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: '/views/components/datepicker-btn.tpl.html',
            scope: {
                dateInfo: '=',
                commit: '='
            },
            link: function(scope, element, attrs) {
                scope.popupInfo = {
                    isOpened:false
                };

                scope.$watch('dateInfo', function(newValue) {
                    scope.popupInfo.isOpened = false;
                },true);
            }
        }
    })
    .filter('dateInfo', function($filter) {
        return function(dateInfo) {
            var result;
            if (dateInfo.urgent) {
                result =  'urgent';
            } else if (dateInfo.date) {
                result =  $filter('date')(dateInfo.date);
            } else {
                result =  'no due date';
            }
            return result;
        };
    })
    .filter('dateInfoClass', function($filter) {
        return function(dateInfo) {
            var str = $filter('dateInfo')(dateInfo);
            if (str === 'urgent') {
                return 'warning';
            } else if (str === 'no due date') {
                return 'default';
            } else {
                return 'danger';
            }
        };
    });