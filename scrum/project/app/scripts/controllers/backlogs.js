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
                    return Backlogs.forProject($stateParams.projectId, first, max);
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
    .controller('BacklogsListCtrl', ['$scope', '$state', 'projectId', 'backlogs',
        function ($scope, $state, projectId, backlogs) {
            $scope.backlogs = backlogs;
            $scope.project = $state.current.data.project;
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