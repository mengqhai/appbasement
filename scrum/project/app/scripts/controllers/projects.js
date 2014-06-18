angular.module('controllers.projects', ['resources.projects',
        'services.crudRouteProvider',
        'directives.crud.edit',
        'directives.crud.buttons'])
    .config(['$routeProvider', 'crudRouteProvider', function ($routeProvider, crudRouteProvider) {
        $routeProvider.when('/projects', {
            templateUrl: 'views/projects/projects-list.tpl.html',
            controller: 'ProjectViewCtrl',
            resolve: {
                projects: ['Projects', function (Projects) {
                    return Projects.query();
                }]
            }
        });

        crudRouteProvider.routeFor('Projects')
            .whenEdit({
                project: ['Projects', '$route', function (Projects, $route) {
                    // resolve watches promise, so we must return a promise
                    return Projects.get({projectId: $route.current.params.itemId}).$promise;
                }]
            });

    }])
    .controller('ProjectViewCtrl', ['$scope', '$location', 'projects', function ($scope, $location, projects) {
        $scope.projects = projects;

        $scope.manageBacklog = function (project) {
            $location.path('/projects/' + project.id + '/backlogs')
        }
    }])
    .controller('ProjectsEditCtrl', ['$scope', 'project', function ($scope, project) {
        $scope.project = project;


    }]);