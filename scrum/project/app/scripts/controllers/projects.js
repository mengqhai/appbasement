angular.module('controllers.projects', ['resources.projects',
        'services.crudRouteProvider',
        'directives.crud.edit',
        'directives.crud.buttons',
        'formPatchable',
        'services.notifications'])
    .config(['crudRouteProvider', function (crudRouteProvider) {
        crudRouteProvider.routeFor('Projects', {})
            .whenList({
                projects: ['Projects', function (Projects) {
                    return Projects.query();
                }]
            })
            .whenNew({
                project: ['Projects', function (Projects) {
                    return new Projects();
                }]
            })
            .whenEdit({
                project: ['Projects', '$stateParams', function (Projects, $stateParams) {
                    // resolve watches promise, so we must return a promise
                    return Projects.get({projectId: $stateParams.itemId}).$promise;
                }]
            });

    }])
    .controller('ProjectsListCtrl', ['$scope', '$state', 'projects', function ($scope, $state, projects) {
        $scope.projects = projects;

        $scope.manageBacklog = function (project) {
            $state.get('projects.backlogs.list').data.project = project;
            $state.go('projects.backlogs.list', {projectId: project.id})
        };

        $scope.new = function () {
            $state.go('projects.new');
        }
    }])
    .controller('ProjectsEditCtrl', [
        '$scope',
        '$state',
        '$stateParams',
        'project',
        'notifications',
        'breadcrumbs',
        function ($scope, $state, $stateParams, project, notifications, breadcrumbs) {
            // projects is inherited from parent state's resolve
            $scope.project = project;

            // for states that needs to dynamically update the breadcrumbs
            if (project.name) {
                // we don't do this for state projects.new
                breadcrumbs.updateTitleForPath($state.current.name, project.name);
            }


            $scope.onSave = function (project) {
                notifications.growl('Project ' + $scope.project.name + ' saved successfully.', 'success', -1);
                $state.go('projects.list', {}, {reload: true});
            };

            $scope.onDelete = function (project) {
                notifications.growl('Project ' + $scope.project.name + ' deleted.', 'success', -1);
                $state.go('projects.list', {}, {reload: true})
            };

            $scope.onError = function (project) {
                notifications.growl('Failed to edit project ' + $scope.project.name, 'error', -1);
            }

            $scope.breadcrumbLabel = function (last, current) {
                if (last === 'projects' && project.id == current) {
                    return project.name;
                }
            }

        }]);