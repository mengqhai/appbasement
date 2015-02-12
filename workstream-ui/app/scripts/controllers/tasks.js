angular.module('controllers.tasks', ['resources.tasks'])
    .controller('TasksController', ['$scope', 'Tasks', function ($scope, Tasks) {
        $scope.myTaskCount = Tasks.countMyTasks();
    }]);