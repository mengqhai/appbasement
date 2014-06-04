angular.module('services.crudRouteProvider', ['ngRoute'])
    // not a true provider, but a provider helper to config the $routeProvider more conveniently
    // resource template path convention: views/<resourceName>/<resourceName>-<operation>.tpl.html
    // controller convention: <ResourceName><Operation>Ctrl
    .provider('crudRoute', ['$routeProvider', function ($routeProvider) {
        this.$get = angular.noop;
        // we never need to get the crudRoute service so nothing needs to do in $get

        //this function is the key part of this "provider helper".
        // We use it to create routes for CRUD operations.  We give it some basic information about
        // the resource and the urls then it it returns our own special routeProvider.
        this.routeFor = function (resourceName, routePrefix) {
            var templateUrlPrefix = 'views/'

            var baseUrl = resourceName.toLowerCase();
            var baseRoute = '/' + resourceName.toLowerCase();

            // Prepend the urlPrefix if available
            if (angular.isString(templateUrlPrefix) && templateUrlPrefix !== '') {
                baseUrl = templateUrlPrefix + '/' + baseUrl;
            }

            // Prepend the routePrefix if provided
            if (routePrefix !== null && routePrefix !== undefined && routePrefix !== '') {
                baseRoute = '/' + routePrefix + baseRoute;
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

            // This is the object that our `routesFor()` function returns.  It decorates `$routeProvider`,
            // delegating the `when()` and `otherwise()` functions but also exposing some new functions for
            // creating CRUD routes.  Specifically we have `whenList(), `whenNew()` and `whenEdit()`.
            var routeWrapper = {
                // Pass-through to `$routeProvider.when()`
                when: function (path, route) {
                    $routeProvider.when(path, route);
                    return routeWrapper;
                },
                // Pass-through to '$routeProvider.otherwise()'
                otherwise: function (params) {
                    $routeProvider.otherwise(params);
                    return routeWrapper;
                },
                // Create a route that will handle showing a list of items
                whenList: function (resolveFns) {
                    $routeProvider.when(baseRoute, {
                        templateUrl: templateUrl('List'),
                        controller: controllerName('List'),
                        resolve: resolveFns
                    });
                    return routeWrapper;
                },
                // Create a route that will handle creating a new item
                whenNew: function(resolveFns) {
                    $routeProvider.when(baseRoute+'/new', {
                        templateUrl: templateUrl('Edit'),
                        controller: controllerName('Edit'),
                        resolve: resolveFns
                    });
                    return routeWrapper;
                },
                // Create a route that will handle editing an existing item
                whenNew: function(resolveFns) {
                    $routeProvider.when(baseRoute+'/:itemId', {
                        templateUrl: templateUrl('Edit'),
                        controller: controllerName('Edit'),
                        resolve: resolveFns
                    });
                    return routeWrapper;
                },
                $routeProvider: $routeProvider
            };
            return routeWrapper;
        };
    }]);