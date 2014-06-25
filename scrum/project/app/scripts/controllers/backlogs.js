angular.module('controllers.backlogs', ['resources.backlogs'])
    .config(['crudRouteProvider', function (crudRouteProvider) {
        var first = 0, max = 5;

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
                    b.projectId = $stateParams.projectId;
                    return b;
                }]
            })
            .whenEdit({
                backlog: ['$stateParams', 'Backlogs', function ($stateParams, Backlogs) {
                    return Backlogs.get({
                        backlogId: $stateParams.itemId
                    }).$promise;
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
        function ($scope, $state, projectId, backlogs, backlogCount, Backlogs) {
            $scope.backlogs = backlogs;
            $scope.itemsPerPage = 5;
            $scope.currentPage = 1
            $scope.backlogCount = backlogCount;
            $scope.project = $state.current.data.project;

            $scope.listBacklogs = function (updateCount, first, max) {
                    $scope.backlogs = Backlogs.forProject($scope.project.id, first, max);
                    if (updateCount)
                        $scope.backlogCount = Backlogs.forProjectCount($scope.selectedProject.id);
            };
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
        }])
    .controller('BacklogsEditCtrl', ['$scope', '$state', 'backlog', 'notifications', 'projectId',
        function ($scope, $state, backlog, notifications, projectId) {
            $scope.backlog = backlog;


            $scope.onSave = function (backlog) {
                notifications.growl('Backlog ' + $scope.backlog.name + ' saved successfully.', 'success', -1);
                $state.go('projects.backlogs.list', {projectId: projectId}, {reload: true});
            };

            $scope.onDelete = function (backlog) {
                notifications.growl('Backlog ' + $scope.backlog.name + ' deleted.', 'success', -1);
                $state.go('projects.backlogs.list', {projectId: projectId}, {reload: true})
            };

            $scope.onError = function (backlog) {
                notifications.growl('Failed to edit backlog ' + $scope.backlog.name, 'error', -1);
            }
        }]);