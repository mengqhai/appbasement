'use strict';

/**
 * @ngdoc function
 * @name workstreamUiApp.controller:AboutCtrl
 * @description
 * # AboutCtrl
 * Controller of the workstreamUiApp
 */
angular.module('workstreamUiApp')
    .controller('AboutCtrl', ['$scope', 'Users', 'envVars', function ($scope, Users, envVars) {
        $scope.awesomeThings = [
            'HTML5 Boilerplate',
            'AngularJS',
            'Karma'
        ];
    }]);
