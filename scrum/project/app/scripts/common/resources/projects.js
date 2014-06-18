angular.module('resources.projects', ['ngResource', 'env'])
    .factory('Projects', ['$resource', 'envConstants', function ($resource, envConstants) {
        var Projects = $resource(envConstants.REST_URL + '/projects/:projectId', {projectId: '@id'},
            {
                doPut: {
                    method: 'PUT'//,
                    //params: { withCredentials: true}
                },
                doPatch: {
                    method: 'PATCH'
                }
            });

        Projects.prototype.$update = function(data) {
            if (data.id === undefined) {
                data.id = this.id;
            }
          return Projects.doPatch(data);
        };

        return Projects;
    }]);