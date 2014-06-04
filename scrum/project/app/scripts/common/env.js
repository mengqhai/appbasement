// A module with environment configurations defined.
angular.module('env', [])
    .constant('URL_BASE', 'http://localhost:8081/angularJsPlay/')
    .factory('envConstants', ['URL_BASE', function (URL_BASE) {
        return {
            URL_BASE: URL_BASE,
            REST_URL: URL_BASE + 'rest',
            LOGIN_URL: URL_BASE + 'rest/login',
            LOGOUT_URL: URL_BASE + 'rest/logout?noRedirect'
        };
    }]);
