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

var DatepickerCtrl = function ($scope) {
    $scope.myText = 'Not Selected';
    $scope.current = '';
    $scope.updateMyText = function (date) {
        $scope.myText = 'Selected';
    }
}
bookplayApp.controller('DatepickerCtrl', DatepickerCtrl);

var ListCtrl = function ($scope, filterService) {
    $scope.filterService = filterService;
    $scope.teamList = [
        {id: 1, name: 'Dallas Mavericks', sport: 'Basketball', city: 'Dallas', featured: true},
        {id: 2, name: 'Dallas Cowboys', sport: 'Football', city: 'Dallas', featured: false},
        {id: 3, name: 'New York Knicks', sport: 'Basketball', city: 'New York', featured: false},
        {id: 4, name: 'Brooklyn Nets', sport: 'Basketball', city: 'New York', featured: false},
        {id: 5, name: 'New York Jets', sport: 'Football', city: 'New York', featured: false},
        {id: 6, name: 'New York Giants', sport: 'Football', city: 'New York', featured: true},
        {id: 7, name: 'Los Angeles Lakers', sport: 'Basketball', city: 'Los Angeles', featured: true},
        {id: 8, name: 'Los Angeles Clippers', sport: 'Basketball', city: 'Los Angeles', featured: false},
        {id: 9, name: 'Dallas Stars', sport: 'Hockey', city: 'Dallas', featured: false},
        {id: 10, name: 'Boston Bruins', sport: 'Hockey', city: 'Boston', featured: true}
    ]
}
bookplayApp.controller('ListCtrl', ListCtrl);
bookplayApp.controller('FilterCtrl', function($scope, filterService) {
    $scope.filterService = filterService;
});




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






