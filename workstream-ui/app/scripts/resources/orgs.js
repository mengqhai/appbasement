angular.module('resources.orgs', ['env', 'resources.users'])
    .factory('OrgCache', ['$cacheFactory', function ($cacheFactory) {
        return $cacheFactory('OrgCache');
    }])
    .factory('Orgs', ['$resource', 'envConstants', 'envVars', 'OrgCache', 'UserCache', function ($resource, envConstants, envVars, OrgCache, UserCache) {
        var paramDefault = {
            api_key: envVars.getApiKey
        };
        var myKey = 'com.workstream.orgs._my';
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
                        ;
                        OrgCache.put(myKey, response.resource);
                        return response;
                    }
                }
            },
            getMyAdminOrgs: {
                method: 'GET',
                isArray: true ,
                params: {
                    administratedOnly: true
                }
            },
            getProjectsInOrg: {
                url: envConstants.REST_BASE + '/orgs/:orgId/projects',
                method: 'GET',
                isArray: true
            },
            getUsersInOrg: {
                url: envConstants.REST_BASE + '/orgs/:orgId/users',
                method: 'GET',
                isArray: true,
                interceptor: function (response) {
                    if (angular.isArray(response.data)) {
                        angular.forEach(response.data, function (idx, user) {
                            UserCache.put(user.id, user)
                        })
                    }
                }
            }
        });

        Orgs.getUsersInOrgWithCache = function (params) {
            var orgUsers = OrgCache.get('orgUsers|' + params.orgId);
            if (!orgUsers) {
                orgUsers = Orgs.getUsersInOrg(params);
                OrgCache.put('orgUsers|' + params.orgId, orgUsers);
            }
            return orgUsers;
        }

        Orgs.getWithCache = function (param) {
            var org = OrgCache.get(param.orgId);
            if (!org) {
                org = Orgs.get(param);
                OrgCache.put(param.orgId, org);
            }
            return org;
        };
        Orgs.getMyOrgsWithCache = function () {
            var myOrgs = OrgCache.get(myKey);
            if (!myOrgs) {
                myOrgs = Orgs.getMyOrgs();
            }
            // Orgs.getMyOrgs() already put it to cache
            return myOrgs;
        }
        return Orgs;
    }]);