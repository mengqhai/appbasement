angular.module('resources.attachments', ['env'])
    .factory('Attachments', ['$resource', 'envConstants', 'envVars', function ($resource, envConstants, envVars) {
        var homeUrl = envConstants.REST_BASE + '/attachments/:attachmentId';
        var Attachments = $resource(homeUrl, {
            api_key: envVars.getApiKey
        }, {

        });
        Attachments.getDownloadUrl = function (attachmentId) {
            return envConstants.REST_BASE + '/attachments/' + attachmentId + '/content?api_key='+envVars.getApiKey();
        }
        return Attachments;
    }]);