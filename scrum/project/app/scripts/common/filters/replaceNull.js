angular.module('filters', [])
    .filter('replaceNull', function () {
        return function (str, replaceWith) {
            if (str) {
                return str;
            } else {
                return replaceWith || 'Unassigned';
            }
        };
    })