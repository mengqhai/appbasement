'use strict';

/**
 * @ngdoc function
 * @name workstreamUiApp.controller:AboutCtrl
 * @description
 * # AboutCtrl
 * Controller of the workstreamUiApp
 */
angular.module('workstreamUiApp')
  .controller('AboutCtrl','Users', 'envVar', function ($scope, Users, envVar) {
    $scope.awesomeThings = [
      'HTML5 Boilerplate',
      'AngularJS',
      'Karma'
    ];
  });
