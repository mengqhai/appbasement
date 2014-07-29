angular.module('components.user-picker', ['components.dropdown-popup', 'ui.bootstrap.buttons',
        'ngAnimate'])
    .directive('userPickerPanel', function($log) {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: '/views/components/user-picker-panel.tpl.html',
            scope: {
                users: '=',
                selected: '=',
                commit: '=' // this method must return a promise
            },
            link: function(scope, ele, attrs) {
                scope.searchInputId = 'user-picker-search-'+scope.$id;

                scope.unassigned = {username:'Unassigned', email:'Unassigned'};

                scope._selected = (angular.isDefined(scope.selected) && scope.selected) ? scope.selected : scope.unassigned;

                scope.select = function(item) {
                    scope._selected = item;
                    scope.save();
                };

                var updateSelect = function() {
                    if (angular.isDefined(scope.selected)) {
                        scope.selected = scope._selected === scope.unassigned ? null : scope._selected;
                    }
                }

                scope.save = function() {
                    if (scope.commit) {
                        var promise = scope.commit(scope.date);
                        if (promise) {
                            promise.then(updateSelect, function(msg) {
                                $log.error("user-picker failed to commit:"+msg);
                            });
                        }
                    } else {
                        updateSelect();
                    }

                }



                scope.filterUsers = function(user) {
                    if (!scope.query) {
                        return true;
                    }
                    return (user.username.toLowerCase().indexOf(scope.query.toLowerCase()) !== -1 ||
                        user.email.toLowerCase().indexOf(scope.query.toLowerCase())!== -1);
                }


            }
        };
    })
    .directive('userPickerBtn', function() {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: '/views/components/user-picker-btn.tpl.html',
            scope: {
                users: '=',
                selectedInfo: '=',
                commit: '='
            },
            link: function(scope, ele, attrs) {
                scope.popupInfo = {
                    isOpened:false
                };

                if (!angular.isDefined(scope.selectedInfo)) {
                    scope.selectedInfo = {
                        selected : null
                    };
                };
                scope.$watch('selectedInfo', function(newValue) {
                    scope.popupInfo.isOpened = false;
                }, true)
            }
        }
    })
    .filter('selectedInfoUsername', function() {
        return function(selectedInfo) {
            if (selectedInfo.selected === null) {
                return 'Unassigned';
            } else {
                return selectedInfo.selected.username;
            }
        }
    });