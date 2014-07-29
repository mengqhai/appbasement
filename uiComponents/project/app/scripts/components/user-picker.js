angular.module('components.user-picker', ['components.dropdown-popup', 'ui.bootstrap.buttons',
        'ngAnimate'])
    .directive('userPickerPanel', function() {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: '/views/components/user-picker-panel.tpl.html',
            scope: {
                users: '=',
                selected: '='
            },
            link: function(scope, ele, attrs) {
                scope.searchInputId = 'user-picker-search-'+scope.$id;

                scope.unassigned = {username:'Unassigned', email:'Unassigned'};

                scope._selected = (angular.isDefined(scope.selected) && scope.selected) ? scope.selected : scope.unassigned;

                scope.select = function(item) {
                    scope._selected = item;
                    scope.save();
                };

                scope.save = function() {
                    if (angular.isDefined(scope.selected)) {
                        scope.selected = scope._selected === scope.unassigned ? null : scope._selected;
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
                selectedInfo: '='
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