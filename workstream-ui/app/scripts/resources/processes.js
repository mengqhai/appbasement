angular.module('resources.processes', ['env'])
    .factory('Processes', ['$resource', 'envConstants', 'envVars', function ($resource, envConstants, envVars) {
        var homeUrl = envConstants.REST_BASE + '/processes/:processId';
        var archHomeUrl = envConstants.REST_BASE + '/archives/processes';
        var Processes = $resource(homeUrl, {
            api_key: envVars.getApiKey
        }, {
            getStartedByMe: {
                method: 'GET',
                url: envConstants.REST_BASE + '/processes/_startedByMe',
                isArray: true
            },
            getInvolvesMe: {
                method: 'GET',
                url: envConstants.REST_BASE + '/processes/_involvedMe',
                isArray: true
            },
            getArchStartedByMe: {
                method: 'GET',
                url: archHomeUrl + '/_startedByMe',
                isArray: true
            },
            getArchInvolvesMe: {
                method: 'GET',
                url: archHomeUrl + '/_involvedMe',
                isArray: true
            },
            getArchive: {
                method: 'GET',
                url: archHomeUrl + '/:processId'
            },
            getArchiveTasks: {
                method: 'GET',
                url: envConstants.REST_BASE + '/archives/processes/:processId/tasks',
                isArray: true
            },
            getVars: {
                method: 'GET',
                url: homeUrl + '/vars'
            },
            getArchVars: {
                method: 'GET',
                url: archHomeUrl + '/:processId/vars'
            }
        })
        Processes.getDiagramUrl = function (processId) {
            return envConstants.REST_BASE + '/processes/' + processId + '/diagram?api_key=' + envVars.getApiKey();
        }
        Processes.getArchDiagramUrl = function (processId) {
            return archHomeUrl + '/' + processId + '/diagram?api_key=' + envVars.getApiKey();
        }

        Processes.createLoader = function (status) {
            if (status !== 'archived') {
                return {
                    getStartedByMe: Processes.getStartedByMe,
                    getInvolvesMe: Processes.getInvolvesMe,
                    get: Processes.get,
                    getArchive: Processes.getArchive,
                    getDiagramUrl: Processes.getDiagramUrl,
                    getArchiveTasks: Processes.getArchiveTasks,
                    getVars: Processes.getVars
                }
            }
            else {
                return {
                    getStartedByMe: Processes.getArchStartedByMe,
                    getInvolvesMe: Processes.getArchStartedByMe,
                    get: Processes.getArchive,
                    getArchive: Processes.getArchive,
                    getDiagramUrl: Processes.getArchDiagramUrl,
                    getArchiveTasks: Processes.getArchiveTasks,
                    getVars: Processes.getArchVars
                }
            }
        };
        return Processes;
    }])