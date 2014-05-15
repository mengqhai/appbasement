'use strict';

angular.module('scrumApp')
    .controller('MainCtrl', function ($scope, $modal, $log) {
        $scope.awesomeThings = [
            'HTML5 Boilerplate',
            'AngularJS',
            'Karma'
        ];

        $scope.items = $scope.awesomeThings;
        $scope.open = function (size) {
            var modalInstance = $modal.open({
                templateUrl: 'views/dialog_content.html',
                size: size,
                controller:'ModalInstanceCtrl',
                resolve: {
                    items: function () {
                        return $scope.awesomeThings;
                    }
                }
            });

            modalInstance.result.then(function (selectedItem) {
                $scope.selected = selectedItem;
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        }
    })
    .controller("ModalInstanceCtrl", function($scope, $modalInstance, items) {
        $scope.items = items;
        $scope.selected = {
            item: $scope.items[0]
        };

        $scope.ok = function () {
            $modalInstance.close($scope.selected.item);
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    });
