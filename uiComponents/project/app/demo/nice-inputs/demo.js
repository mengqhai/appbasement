angular.module('nice-checkbox-demo', [])
    .controller('DemoCtrl', function ($scope) {
        $scope.selected1 = true;
        $scope.selected2 = false;
        $scope.selected3 = false;
        $scope.selectedOption = 'Option1';
        $scope.colorPicked = {value:"red"};
        $scope.colors = [null, 'brown', 'orange', 'yellow', 'green-1', 'green-2', 'blue-1', 'blue-2', 'blue-3',
            'violet', 'rose', 'red'];
    })
    .controller('SearchCtrl', function($scope) {
        $scope.options = ['Dapibus ac facilisis in', 'Morbi leo risus', 'Porta ac consectetur ac', 'Vestibulum at eros'
            ,'Vestibulum at eros xxx', 'Cras justo odio', 'item 1', 'item 2', 'item 3' , 'item 4', 'item 5'];

        $scope.select = function(item) {
            $scope.selected = item;
        }

    });