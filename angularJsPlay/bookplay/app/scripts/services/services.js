/**
 * Created with JetBrains WebStorm.
 * User: qinghai
 * Date: 14-3-3
 * Time: 下午4:56
 * To change this template use File | Settings | File Templates.
 */
var bookplayApp = angular.module('bookplayApp');
bookplayApp.factory('RestGreeting', ['$resource', function ($resource) {
    var url = 'http://rest-service.guides.spring.io/greeting';
    return $resource(url);
}])