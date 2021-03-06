angular.module('resources.groups', ['env'])
    .factory('Groups', ['$resource', 'envConstants', 'envVars', function ($resource, envConstants, envVars) {
        var homeUrl = envConstants.REST_BASE + '/groups/:groupId';
        var Groups = $resource(homeUrl, {
            api_key: envVars.getApiKey
        }, {
            getMembers: {
                url: homeUrl + '/users',
                isArray: true
            },
            patch: {
                method: 'PATCH',
                url: homeUrl
            },
            deleteMember: {
                method: 'DELETE',
                url: homeUrl+'/users/:userIdBase64'
            },
            addMember: {
                method: 'PUT',
                url: homeUrl+'/users/:userIdBase64'
            },
            save: {
                method: 'POST',
                url: envConstants.REST_BASE + '/orgs/:orgId/groups'
            }
        });
        return Groups;
    }]);