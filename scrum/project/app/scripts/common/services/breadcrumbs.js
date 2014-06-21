angular.module('services.breadcrumbs', [])
    // a very simple breadcrumbs
    // the label for id can be fixed if use ui-router https://gist.github.com/lossendae/8356512
    // http://stackoverflow.com/questions/21970406/how-to-make-automated-dynamic-breadcrumbs-with-angularjs-angular-ui-router
    .factory('breadcrumbs', ['$rootScope', '$state', '$interpolate', function ($rootScope, $state, $interpolate) {
        var breadcrumbs = [];

        var breadcrumbsService = {};
        //we want to update breadcrumbs only when a route is actually changed
        //as $location.path() will get updated imediatelly (even if route change fails!)
        $rootScope.$on('$stateChangeSuccess', function (event, toState) {
            breadcrumbs.length = 0;
            var getBreadcrumb = function (theState) {
                var b = null;
                var state = $state.get(theState);
                if (state.data && state.data.title) {
                    b = {};
                    b.name = state.data.title;
                    b.path = state.name;
                }

                if (state.abstract) {
                    // unable to route to abstract state, so fall back to its child list state
                    var list = $state.get(state.name+'.list');
                    if (list) {
                        b.path = state.name+'.list';
                    } else {
                        throw new Error('Breadcrumbs: unable to find child list state '+state.name+'.list for abstract state '+state.name);
                    }
                }
                return b;
            }

            if (toState.name !== 'home') {
                var b = getBreadcrumb('home');
                if (b) {
                    breadcrumbs.unshift(b);
                } else {
                    breadcrumbs.unshift({name: 'Home', path: 'home'});
                }
            }
            angular.forEach($state.$current.path, function (pathEle) {
                var b = getBreadcrumb(pathEle);
                b && breadcrumbs.push(b);
            });
        });

        breadcrumbsService.getAll = function () {
            return breadcrumbs;
        };

        breadcrumbsService.getLast = function () {
            return breadcrumbs[breadcrumbs.length - 1] || {};
        }

        return breadcrumbsService;
    }]);