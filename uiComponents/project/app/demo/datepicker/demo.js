angular.module('datepicker-demo',['ui.bootstrap','ui.bootstrap.datepicker', 'components.datepicker-panel'])
    .controller('DemoCtrl', function($scope) {

        $scope.date1 = new Date();
        $scope.dateInfo = {
            date: new Date(),
            urgent: false
        }

    });