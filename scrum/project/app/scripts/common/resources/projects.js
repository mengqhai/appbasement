angular.module('resources.projects',['ngResource', 'env'])
    .factory('Projects', ['$resource', 'envConstants', function($resource, envConstants) {
        var Projects =  $resource(envConstants.REST_URL+'/projects/:projectId', {projectId: '@id'});
        return Projects;
    }]);