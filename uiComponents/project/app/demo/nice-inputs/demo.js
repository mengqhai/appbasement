angular.module('nice-checkbox-demo',[])
    .controller('DemoCtrl', function($scope) {
        $scope.selected1=true;
        $scope.selected2=false;
        $scope.selected3=false;
        $scope.selectedOption = 'Option1';
    });