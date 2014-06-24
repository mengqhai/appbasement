angular.module('controllers.backlogs', ['resources.backlogs'])
    .config(['crudRouteProvider', function (crudRouteProvider) {
        var first = 0, max = 5;

        crudRouteProvider.routeFor('Backlogs', {
            projectId: ['$stateParams', function ($stateParams) {
                return $stateParams.projectId;
            }]
        }, '/projects/:projectId', 'projects')
            .whenList({
                backlogs: ['$stateParams', 'Backlogs', function ($stateParams, Backlogs) {
                    return Backlogs.forProject($stateParams.projectId, first, max);
                }]
            })
            .whenNew({
                backlog: ['Backlogs', function(Backlogs) {
                    return new Backlogs();
                }]
            });
    }])
    .controller('BacklogsListCtrl', ['$scope', '$state', 'projectId', 'backlogs',
        function ($scope, $state, projectId, backlogs) {
            $scope.backlogs = backlogs;
            $scope.project = $state.current.data.project;
            $scope.new = function() {
                $state.go('projects.backlogs.new')
            }
        }])
    .controller('BacklogsEditCtrl', ['$scope', 'backlog', function($scope, backlog) {
        $scope.backlog = backlog;
    }]);