angular.module('env', [])
    .constant('URL_BASE', 'http://localhost:8080/workstream-rest')
    .factory('envConstants', ['URL_BASE', function (URL_BASE) {
        return {
            URL_BASE: URL_BASE,
            REST_BASE: URL_BASE+'/rest'
        };
    }])
    .factory('envVars', [function() {
        return {
            apiKey:""
        }
    }]);
