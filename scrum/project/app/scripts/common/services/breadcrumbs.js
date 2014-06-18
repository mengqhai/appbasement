angular.module('services.breadcrumbs', [])
    // a very simple breadcrumbs
    // the label for id can be fixed if use ui-router https://gist.github.com/lossendae/8356512
    // http://stackoverflow.com/questions/21970406/how-to-make-automated-dynamic-breadcrumbs-with-angularjs-angular-ui-router
    .factory('breadcrumbs', ['$rootScope', '$location', function ($rootScope, $location) {
        var breadcrumbs = [];

        var breadcrumbsService = {};
        //we want to update breadcrumbs only when a route is actually changed
        //as $location.path() will get updated imediatelly (even if route change fails!)
        $rootScope.$on('$routeChangeSuccess', function (event, current) {
            var pathElements = $location.path().split('/'), result = [], i;
            var breadcrumbPath = function (index) {
                return '/' + (pathElements.slice(0, index + 1)).join('/');
            };
            pathElements.shift();
            for (i = 0; i < pathElements.length; i++) {
                var name = pathElements[i];
                var label = name;
                // workaround to convert to label
                if (i>0 && current.scope.breadcrumbLabel) {
                    label = current.scope.breadcrumbLabel(pathElements[i-1], pathElements[i]) || name;
                }
                result.push({name: label, path: breadcrumbPath(i)});
            }
            result.unshift({name:'home', path:'/'});

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