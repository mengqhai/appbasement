angular.module('resources.backlogs', ['ngResource', 'env'])
    .factory('Backlogs', ['$resource', 'envConstants', function ($resource, envConstants) {
        var Backlogs = $resource(envConstants.REST_URL + '/:parent/:parentId/backlogs/:backlogId',
            {
                parent: '',
                backlogId: '@id'
            },
            {
                count: {
                    method: 'GET',
                    isArray: false,
                    params: {count: ''}
                }
            });    // by default, parent path is ''
        Backlogs.forProject = function (projectId, first, max) {
            var data = {parent: 'projects', parentId: projectId};
            if (first !== undefined && max !== undefined) {
                data.first = first;
                data.max = max;
            }
            return Backlogs.query(data);
        };
        Backlogs.forProjectCount = function (projectId) {
            return Backlogs.count({parent: 'projects', parentId: projectId, count: ''});
        };

        return Backlogs;
    }]);