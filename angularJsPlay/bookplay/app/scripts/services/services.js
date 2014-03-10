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

services.factory('Paginator', function () {
    // Despite being a factory, the user of the service gets a new
    // Paginator every time he calls the service. This is because
    // we return a function that provides an object when executed
    // The fetchfunction expects the following signature:
    // fetchFunction(offset, limit, callback);
    return function (fetchFunction, pageSize) {
        pageSize = pageSize || 10;
        var paginator = {
            hasNextVar: false,
            currentPageItems: [],
            currentOffset: 0,
            next: function () {
                if (this.hasNextVar) {
                    this.currentOffset += pageSize;
                    this._load();
                }
            },
            hasNext: function () {
                return this.hasNextVar;
            },
            previous: function () {
                if (this.hasPrevious()) {
                    this.currentOffset -= pageSize;
                    this._load();
                }
            },
            hasPrevious: function () {
                return this.currentOffset !== 0;
            },
            _load: function () {
                var self = this;
                fetchFunction(this.currentOffset, pageSize + 1, function (items) {
                    self.currentPageItems = items.slice(0, pageSize);
                    self.hasNextVar = items.length === pageSize + 1;
                });
            }
        };

        // Load the first page
        paginator._load();
        return paginator;
    };
});

