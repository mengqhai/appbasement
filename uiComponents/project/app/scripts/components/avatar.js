angular.module('components.avatar', [])
    .directive('avatar', function() {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                user: '='
            },
            templateUrl: '/views/components/avatar.tpl.html',
            link: function(scope, ele, attrs) {
                // big, medium, normal, small, micro
                scope.aSize=attrs['size'] ? attrs['size']:'small';

                scope.square = angular.isDefined(attrs['square']);

                scope.getUsernameShort = function() {
                    if (scope.user && scope.user.username) {
                        return scope.user.username[0].toUpperCase();
                    } else if (scope.user) {
                        return '@';
                    } else {
                        return null;
                    }
                }
            }
        }
    })