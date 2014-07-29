angular.module('components.user-picker', [])
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

                scope._selected = scope.users[0];

                scope.select = function(item) {
                    scope._selected = item;
                    scope.save();
                };

                scope.save = function() {
                    if (angular.isDefined(scope.selected))
                        scope.selected = scope._selected;
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