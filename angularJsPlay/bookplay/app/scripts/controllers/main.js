'use strict';

var bookplayApp = angular.module('bookplayApp');

var HttpGreetingCtrl = function ($scope, $http) {
    var url = 'http://rest-service.guides.spring.io/greeting';
    $http.get(url).success(function (data, status, headers, config) {
        $scope.greeting = data;
    });
}
bookplayApp.controller('HttpGreetingCtrl', HttpGreetingCtrl);

var RestGreetingCtrl = function ($scope, $resource) {

}
bookplayApp.controller('RestGreetingCtrl', RestGreetingCtrl);

bookplayApp.controller('MainCtrl', function ($scope) {
    $scope.awesomeThings = [
        'HTML5 Boilerplate',
        'AngularJS',
        'Karma'
    ];
});

