angular.module('services.breadcrumbs', [])
    // a very simple breadcrumbs
    // the label for id can be fixed if use ui-router https://gist.github.com/lossendae/8356512
    // http://stackoverflow.com/questions/21970406/how-to-make-automated-dynamic-breadcrumbs-with-angularjs-angular-ui-router
    .factory('breadcrumbs', ['$rootScope', '$state', function ($rootScope,$state) {
        var breadcrumbs = [];

        var breadcrumbsService = {};
        //we want to update breadcrumbs only when a route is actually changed
        //as $location.path() will get updated imediatelly (even if route change fails!)
        $rootScope.$on('$stateChangeSuccess', function (event, toState) {
            breadcrumbs.length=0;
            var pathElements = toState.name.split('.'), result = [], i;
            var breadcrumbPath = function (index) {
                return '/' + (pathElements.slice(0, index + 1)).join('/')+'/';
            };
            for (i = 0; i < pathElements.length; i++) {
                var name = pathElements[i];
                var label = name;
                result.push({name: label, path: breadcrumbPath(i)});
            }
            if (toState.name!=='home') {
                result.unshift({name:'home', path:'/'});
            }
            breadcrumbs = result;
        });

        breadcrumbsService.getAll = function() {
            return breadcrumbs;
        };

        breadcrumbsService.getLast = function() {
            return breadcrumbs[breadcrumbs.length - 1] || {};
        }

        return breadcrumbsService;
    }]);