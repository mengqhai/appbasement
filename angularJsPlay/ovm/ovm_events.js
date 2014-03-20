/**
 * Created with JetBrains WebStorm.
 * User: qinghai
 * Date: 14-3-20
 * Time: 上午10:29
 * To change this template use File | Settings | File Templates.
 */

angular.module('ovm.events', ['ngResource', 'base64'])
    .factory('Events',function ($resource) {
        var Events = $resource('http://:hostname/ovm/core/wsapi/rest/Event', {
            hostname: '@hostname'
        });
        return Events;
    }).
    constant('OBJECT_TYPES', ['com.oracle.ovm.mgr.ws.model.Vm'])
    .controller('EventsCtrl', function ($scope, $log, Events, OBJECT_TYPES, $base64, $http) {
        $scope.hostname = '10.182.32.59:7001';
        $scope.objTypes = OBJECT_TYPES;
        $scope.selectedObjType = OBJECT_TYPES[0];
        $scope.username = 'admin';
        $scope.password = 'Welcome1';
        $scope.events = [];
        $scope.queryEvent = function () {
            var encoded = $base64.encode($scope.username+":"+$scope.password);
            $log.info(encoded);
            $http.defaults.headers.common['Authorization']='Basic '+encoded;
            Events.query({hostname: $scope.hostname}, function (events) {
                $scope.events = events;
            });
        };
    });
