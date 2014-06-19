angular.module('services.crud', ['services.crudRouteProvider'])
    // the methods can be copied to $scope instance using angular.extend($scope, crudEditMethods('xxxx')
    .factory('crudEditMethods', function() {
        return function(itemName, item, formName, successcb, errorcb) {
            var mixin = {};
            mixin[itemName] = item;
            mixin[itemName+'Copy'] = angular.copy(item);

            mixin.save = function() {

            }
        };
    })
    .factory('crudListMethods', function() {

    });