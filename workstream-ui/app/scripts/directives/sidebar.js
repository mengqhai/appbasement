'use strict';

angular.module('directives.sidebar', [])
    .directive('sidebar', function () {
        return {
            restrict: 'E',
            templateUrl: 'views/sidebar.html'
        };
    });