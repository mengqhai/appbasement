angular.module('controllers.projects',['resources.projects'])
    .config(['$routeProvider', function($routeProvider) {
        $routeProvider.when('/projects', {
            templateUrl: 'views/projects/projects-list.tpl.html',
            controller: 'ProjectViewCtrl',
            resolve: {
                projects:['Projects', function(Projects) {
                    return Projects.query();
                }]
            }
        })
    }])
    .controller('ProjectViewCtrl', ['$scope', '$location', 'projects', function($scope, $location, projects) {
        $scope.projects = projects;
    }])