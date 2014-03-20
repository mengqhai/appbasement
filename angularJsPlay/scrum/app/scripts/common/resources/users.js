angular.module('scrum.resources.users', ['ngResource', 'scrum.config'])
    .factory('Users', ['$resource', 'SCRUM_CONFIG', function ($resource, SCRUM_CONFIG) {
        var userResource = $resource(SCRUM_CONFIG.baseUrl + 'user/:id', {
            id: '@id'
        });
        userResource.prototype.getFullName = function () {
            // just use username for placeholder
            return this.username + ' (' + this.email + ')';
        };
        return userResource;
    }]);
