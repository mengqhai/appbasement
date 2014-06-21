angular.module('services.crudRouteProvider', ['ui.router'])
    // not a true provider, but a provider helper to config the $routeProvider more conveniently
    // resource template path convention: views/<resourceName>/<resourceName>-<operation>.tpl.html
    // controller convention: <ResourceName><Operation>Ctrl
    .provider('crudRoute', ['$stateProvider', function ($stateProvider) {
        this.$get = angular.noop;
        // we never need to get the crudRoute service so nothing needs to do in $get

        //this function is the key part of this "provider helper".
        // We use it to create routes for CRUD operations.  We give it some basic information about
        // the resource and the urls then it it returns our own special routeProvider.
        this.routeFor = function (resourceName, commonResolve) {
            var stateName = resourceName.toLowerCase();
            var templateUrlPrefix = 'views/'

            var baseUrl = resourceName.toLowerCase();
            var baseRoute = '/' + resourceName.toLowerCase();

            // Prepend the urlPrefix if available
            if (angular.isString(templateUrlPrefix) && templateUrlPrefix !== '') {
                baseUrl = templateUrlPrefix +  baseUrl;
            }

            // Create the templateUrl for a route to our resource that does the specified operation.
            // e.g routeFor('Backlogs', 'projects/:projectId'), operation=List
            // templateUrl will be views/backlogs/backlogs-list.tpl.html
            var templateUrl = function (operation) {
                return baseUrl + '/' + resourceName.toLowerCase() + '-' + operation.toLowerCase() + '.tpl.html';
            };
            // Create the controller name for a route to our resource that does the specified operation.
            var controllerName = function (operation) {
                return resourceName + operation + 'Ctrl';
            };

            $stateProvider.state(stateName, {
                abstract: true,
                url: baseRoute,
                template: '<ui-view/>',
                resolve: commonResolve
            });

            // This is the object that our `routesFor()` function returns.  It decorates `$routeProvider`,
            // delegating the `when()` and `otherwise()` functions but also exposing some new functions for
            // creating CRUD routes.  Specifically we have `whenList(), `whenNew()` and `whenEdit()`.
            var routeWrapper = {
                // Create a route that will handle showing a list of items
                // https://github.com/angular-ui/ui-router/wiki/Nested-States-%26-Nested-Views
                // Child states will load their templates into their parent's ui-view.
                // The parent is the list state
                whenList: function (resolveFns) {
                    $stateProvider.state(stateName+".list", {
                        url: '/',
                        templateUrl: templateUrl('List'),
                        controller: controllerName('List'),
                        resolve: resolveFns
                     });
                    return routeWrapper;
                },
                // Create a route that will handle creating a new item
                whenNew: function(resolveFns) {
                    $stateProvider.state(stateName+".new", {
                        // https://github.com/angular-ui/ui-router/wiki/URL-Routing
                        url: '/new',   // /<parentUrl>/new
                        templateUrl: templateUrl('Edit'),
                        controller: controllerName('Edit'),
                        resolve: resolveFns,
                        data: {
                            title: 'New project'
                        }
                    });
                    return routeWrapper;
                },
                // Create a route that will handle editing an existing item
                whenEdit: function(resolveFns) {
                    $stateProvider.state(stateName +".edit", {
                        url: '/:itemId', // /<parentUrl>/:itemId
                        templateUrl: templateUrl('Edit'),
                        controller: controllerName('Edit'),
                        resolve: resolveFns,
                        data: {
                            title: 'Edit project'
                        }
                    });
                    return routeWrapper;
                }
            };
            return routeWrapper;
        };
    }]);