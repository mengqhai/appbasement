angular.module('nice-checkbox-demo',['components.nice-checkbox'])
    .controller('DemoCtrl', function($scope) {
        $scope.selected1=false;
        $scope.selected2=true;
    });