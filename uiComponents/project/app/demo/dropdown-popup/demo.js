angular.module('dropdown-popup-demo', ['components.dropdown-popup', 'ngAnimate'])
    .controller('DemoCtrl', function($scope) {
        $scope.info={
          isOpened: false
        };
        $scope.toggleOpen = function() {
            $scope.info.isOpened = !$scope.info.isOpened;
        }

        $scope.innerBtnClick = function(e) {
            console.info('Inner button clicked');
        };
    });