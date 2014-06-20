angular.module('controllers.projects', ['resources.projects',
        'services.crudRouteProvider',
        'directives.crud.edit',
        'directives.crud.buttons',
        'formPatchable',
        'services.notifications'])
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
            .whenNew({
                project:['Projects', function(Projects) {
                    return new Projects();
                }]
            })
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
        };

        $scope.newProject = function() {
            $location.path('/projects/new');
        }
    }])
    .controller('ProjectsEditCtrl', ['$scope','$location', 'project','notifications', function ($scope,$location, project, notifications) {
        $scope.project = project;

        $scope.onSave = function(project) {
            //notifications.pushForCurrentRoute({msg:'Project saved successfully.', type:'success'});
            notifications.growl('Project '+$scope.project.name+' saved successfully.', 'success');
            $location.path('/projects/');
        };

        $scope.onDelete = function(project) {
            //notifications.pushForCurrentRoute({msg:'Project deleted.', type:'success'});
            notifications.growl('Project '+$scope.project.name+' deleted.', 'success');
            $location.path('/projects/');
        };

        $scope.onError = function(project) {
            //notifications.pushForCurrentRoute({msg:'Failed to edit project.', type: 'error'})
            notifications.growl('Failed to edit project '+$scope.project.name, 'error', -1);
        }

        $scope.breadcrumbLabel = function(last, current) {
            if (last === 'projects' && project.id == current) {
                return project.name;
            }
        }

    }]);