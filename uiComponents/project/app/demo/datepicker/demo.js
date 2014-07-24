angular.module('datepicker-demo',['ui.bootstrap','ui.bootstrap.datepicker', 'components.datepicker-panel',
        'components.dropdown-popup', 'ui.bootstrap.buttons', 'ngAnimate'])
    .controller('DemoCtrl', function($scope) {

        $scope.date1 = new Date();
        $scope.dateInfo = {
            date: new Date(),
            urgent: false
        };
        $scope.popupInfo = {
            isOpened:false
        };

        $scope.toggleOpen = function() {
            $scope.popupInfo.isOpened = !$scope.popupInfo.isOpened;
        }

    });