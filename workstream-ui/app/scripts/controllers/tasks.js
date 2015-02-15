angular.module('controllers.tasks', ['resources.tasks', 'ui.router', 'xeditable', 'ui.select'])
    .controller('TasksController', ['$scope', 'Tasks', function ($scope, Tasks) {
        $scope.myTaskCount = 0;
        $scope.createdByMeCount = 0;
        $scope.candidateTaskCount = 0;

        $scope.loadCounts = function () {
            $scope.myTaskCount = Tasks.countMyTasks();
            $scope.createdByMeCount = Tasks.countCreatedByMe();
            $scope.candidateTaskCount = Tasks.countMyCandidateTasks();
        }

        //$scope.loadCounts();
    }])
    .controller('TaskListController', ['$scope', 'Tasks', '$stateParams', '$modal', function ($scope, Tasks, $stateParams, $modal) {
        $scope.params = $stateParams;
        $scope.tasks = [];
        $scope.loadByType = function () {
            $scope.tasks = Tasks.getByListType({type: $stateParams.listType});
        };
        $scope.loadByType();

        var dialog = null;
        $scope.openDialog = function (task) {
            dialog = $modal.open({
                templateUrl: 'views/taskForm.html',
                controller: 'TaskFormController',
                resolve: {
                    task: function () {
                        return task;
                    }
                },
                scope: $scope
            })
        };
        $scope.closeDialog = function () {
            if (dialog) {
                dialog.dismiss('cancelTask');
            }
        }
    }])
    .controller('TaskFormController', ['$scope', 'Tasks', 'Orgs', 'Users', '$q', 'task', function ($scope, Tasks, Orgs, Users, $q, task) {
        $scope.task = task;
        $scope.org = Orgs.getWithCache({orgId: task.orgId});
        $scope.myOrgs = Orgs.getMyOrgsWithCache();

        $scope.events = Tasks.getEvents({taskId: task.id});
        $scope.getUserPicUrl = Users.getUserPicUrl;
        $scope.updateTask = function (key, value) {
            return Tasks.xedit($scope.task.id, key, value);
        };

        $scope.assignee =  task.assignee ? Users.getWithCache({userIdBase64: btoa(task.assignee)}) : null;
        $scope.getUsersInOrg = function(orgId) {
            $scope.users = Orgs.getUsersInOrgWithCache({orgId: orgId});
            $scope.users.push({
                id: null,
                firstName: 'Unassigned'
            })
        }
        $scope.getUsersInOrg(task.orgId);



        $scope.open = function($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.opened = true;
        };

        $scope.dueDateForPicker = task.dueDate;
        $scope.dueDateError = null;
        $scope.$watch('dueDateForPicker', function(newValue, oldValue) {
            if (newValue == oldValue) {
                return;
            }
            $scope.updateTask('dueDate', newValue).then(function(sucess) {
                $scope.task.dueDate = $scope.dueDateForPicker;
            }, function (error) {
                $scope.dueDateError = error;
            })
        })
    }]);