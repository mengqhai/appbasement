angular.module('resources.orgs', ['env'])
    .factory('OrgCache', ['$cacheFactory', function ($cacheFactory) {
        return $cacheFactory('OrgCache');
    }])
    .factory('Orgs', ['$resource', 'envConstants', 'envVars', 'OrgCache', function ($resource, envConstants, envVars, OrgCache) {
        var paramDefault = {
            api_key: envVars.getApiKey
        };
        var Orgs = $resource(envConstants.REST_BASE + '/orgs/:orgId', paramDefault, {
            getMyOrgs: {
                method: 'GET',
                isArray: true,
                interceptor: {
                    response: function (response) {
                        for (var i = 0; i < response.resource.length; i++) {
                            var org = response.resource[i];
                            OrgCache.put(org.id, org);
                        }
                    }
                }
            },
            getProjectsInOrg: {
                url: envConstants.REST_BASE + '/orgs/:orgId/projects',
                method: 'GET',
                isArray: true
            }
        });

        Orgs.getWithCache = function(param) {
            var org = OrgCache.get(param.orgId);
            if (!org) {
                org = Orgs.get(param);
            }
            OrgCache.put(param.orgId, org);
            return org;
        }
        return Orgs;
    }]);