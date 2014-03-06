'use strict';
/**
 * Created with JetBrains WebStorm.
 * User: qinghai
 * Date: 14-3-3
 * Time: 下午4:56
 * To change this template use File | Settings | File Templates.
 */
var services = angular.module('bookplayApp.services',
    ['ngResource']);

services.factory('RestGreeting', ['$resource', function ($resource) {
    var url = 'http://rest-service.guides.spring.io/greeting';
    return $resource(url);
}])

services.factory('RestPromiseGreeting', ['RestGreeting', '$q', function (RestGreeting, $q) {
    var fun = function () {
        var deferred = $q.defer();
        RestGreeting.get(function (greeting) {
            deferred.resolve(greeting);
        }, function () {
            deferred.reject('Unable to fetch greeting.');
        });
        return deferred.promise;
    }
    return fun;
}])

services.factory('filterService', function () {
    return {
        activeFilters: {},
        searchText: ''
    }
});