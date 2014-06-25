angular.module('controllers.backlogs', ['resources.backlogs'])
    .config(['crudRouteProvider', function (crudRouteProvider) {
        var first = 0, max = 5;

        crudRouteProvider.routeFor('Backlogs')
            .whenList({
                projectId: function () {
                    return null;
                },
                backlogs: ['Backlogs', function (Backlogs) {
                    return Backlogs.query({first: first, max: max}).$promise;
                }],
                backlogCount: ['Backlogs', function (Backlogs) {
                    return Backlogs.count().$promise;
                }]
            });

        crudRouteProvider.routeFor('Backlogs', {
            projectId: ['$stateParams', function ($stateParams) {
                return $stateParams.projectId;
            }]
        }, '/:projectId', 'projects')
            .whenList({
                backlogs: ['$stateParams', 'Backlogs', function ($stateParams, Backlogs) {
                    return Backlogs.forProject($stateParams.projectId, first, max).$promise;
                }],
                backlogCount: ['$stateParams', 'Backlogs', function ($stateParams, Backlogs) {
                    return Backlogs.forProjectCount($stateParams.projectId).$promise;
                }]
            })
            .whenNew({
                backlog: ['$stateParams', 'Backlogs', function ($stateParams, Backlogs) {
                    var b = new Backlogs();
                    b.projectId = parseInt($stateParams.projectId);
                    return b;
                }],
                projects: ['Projects', function (Projects) {
                    return Projects.query().$promise;
                }]
            })
            .whenEdit({
                backlog: ['$stateParams', 'Backlogs', function ($stateParams, Backlogs) {
                    return Backlogs.get({
                        backlogId: $stateParams.itemId
                    }).$promise;
                }],
                projects: ['Projects', function (Projects) {
                    return Projects.query().$promise;
                }]
            });
    }])
    .controller('BacklogsListCtrl', [
        '$scope',
        '$state',
        'projectId',
        'backlogs',
        'backlogCount',
        'Backlogs',
        'Projects',
        function ($scope, $state, projectId, backlogs, backlogCount, Backlogs, Projects) {
            $scope.backlogs = backlogs;
            $scope.itemsPerPage = 5;
            $scope.currentPage = 1
            $scope.backlogCount = backlogCount;

            if (projectId) {
                if ($state.current.data.project && $state.current.data.project.id == projectId) {
                    $scope.project = $state.current.data.project;
                } else {
                    $scope.project = Projects.get({projectId: projectId});
                }

                $scope.listBacklogs = function (updateCount, first, max) {
                    $scope.backlogs = Backlogs.forProject($scope.project.id, first, max);
                    updateCount &&
                    ($scope.backlogCount = Backlogs.forProjectCount($scope.selectedProject.id));
                }
            } else {
                $scope.listBacklogs = function (updateCount, first, max) {
                    $scope.backlogs = Backlogs.query({first: first, max: max});
                    updateCount && ($scope.backlogCount = Backlogs.count());
                }
            }

            // for page switches
            $scope.$watch('currentPage', function (newPage, oldPage) {
                if (oldPage !== newPage) {
                    var first = (newPage - 1) * $scope.itemsPerPage;
                    $scope.listBacklogs(false, first, $scope.itemsPerPage);
                }
            });

            $scope.new = function () {
                $state.go('projects.backlogs.new')
            };
        }
    ])
    .
    controller('BacklogsEditCtrl', ['$scope', '$state', 'backlog', 'notifications', 'projectId', 'projects',
        function ($scope, $state, backlog, notifications, projectId, projects) {
            $scope.backlog = backlog;
            $scope.projects = projects;


            $scope.onSave = function (backlog) {
                notifications.growl('Backlog ' + $scope.backlog.name + ' saved successfully.', 'success', -1);
                $state.go('projects.backlogs.list', {projectId: backlog.projectId}, {reload: true});
            };

            $scope.onDelete = function (backlog) {
                notifications.growl('Backlog ' + $scope.backlog.name + ' deleted.', 'success', -1);
                $state.go('projects.backlogs.list', {projectId: projectId}, {reload: true})
            };

            $scope.onError = function (error) {
                notifications.growl('Failed to edit backlog ' + $scope.backlog.name + ":<br/> " +
                    "<b>"+ error.data.message+"</b>", 'error', -1);
            }
        }]);