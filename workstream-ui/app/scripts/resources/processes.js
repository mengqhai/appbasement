angular.module('resources.processes', ['env'])
    .factory('Processes', ['$resource', 'envConstants', 'envVars', function ($resource, envConstants, envVars) {
        var homeUrl = envConstants.REST_BASE + '/processes/:processId';
        var archHomeUrl = envConstants.REST_BASE + '/archives/processes';
        var Processes = $resource(homeUrl, {
            api_key: envVars.getApiKey
        }, {
            getStartedByMe: {
                url: envConstants.REST_BASE + '/processes/_startedByMe',
                isArray: true
            },
            countStartedByMe: {
                url: envConstants.REST_BASE + '/processes/_startedByMe/_count'
            },
            getInvolvesMe: {
                url: envConstants.REST_BASE + '/processes/_involvedMe',
                isArray: true
            },
            countInvolvesMe: {
                url: envConstants.REST_BASE + '/processes/_involvedMe/_count'
            },
            getArchStartedByMe: {
                url: archHomeUrl + '/_startedByMe',
                isArray: true
            },
            countArchStartedByMe: {
                url: archHomeUrl + '/_startedByMe/_count'
            },
            getArchInvolvesMe: {
                url: archHomeUrl + '/_involvedMe',
                isArray: true
            },
            countArchInvolvesMe: {
                url: archHomeUrl + '/_involvedMe/_count'
            },
            getArchive: {
                url: archHomeUrl + '/:processId'
            },
            getArchiveTasks: {
                url: envConstants.REST_BASE + '/archives/processes/:processId/tasks',
                isArray: true
            },
            getVars: {
                url: homeUrl + '/vars'
            },
            getArchVars: {
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
                    countStartedByMe: Processes.countStartedByMe,
                    countInvolvesMe: Processes.countInvolvesMe,
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
                    countStartedByMe: Processes.countArchStartedByMe,
                    countInvolvesMe: Processes.countArchInvolvesMe,
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