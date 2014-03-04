'use strict';

var bookplayApp = angular.module('bookplayApp.controllers', ['bookplayApp.services']);

var HttpGreetingCtrl = function ($scope, $http) {
    var url = 'http://rest-service.guides.spring.io/greeting';
    $http.get(url).success(function (data, status, headers, config) {
        $scope.greeting = data;
    });
}
bookplayApp.controller('HttpGreetingCtrl', HttpGreetingCtrl);

var RestGreetingCtrl = function ($scope, RestGreeting) {
    $scope.greeting = RestGreeting.get();  // directly put it in template, so no call back needed.
}
bookplayApp.controller('RestGreetingCtrl', RestGreetingCtrl);

var RestPromiseGreetingCtrl = function ($scope, RestPromiseGreeting) {
    var promise = RestPromiseGreeting();
    promise.then(function (greeting) {
        $scope.greeting = greeting;
    });
}


bookplayApp.controller('MainCtrl', function ($scope) {
    $scope.awesomeThings = [
        'HTML5 Boilerplate',
        'AngularJS',
        'Karma'
    ];

    $scope.expanders = [
        {title: 'Click me to expand',
            text: 'Hi there folks, I am the content that was hidden but is now shown.'},
        {title: 'Click this',
            text: 'I am even better text than you have seen previously'},
        {title: 'No, click me!',
            text: 'I am text that should be seen before seeing other texts'}
    ];
});

