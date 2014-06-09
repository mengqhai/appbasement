'use strict';

angular.module('scrumApp')
    .controller('MainCtrl', function ($scope, $modal, $log, $http, loginDialog, Projects, Backlogs) {
        $scope.awesomeThings = [
            'HTML5 Boilerplate',
            'AngularJS',
            'Karma'
        ];

        $scope.items = $scope.awesomeThings;
        $scope.open = function (size) {
            var modalInstance = $modal.open({
                templateUrl: 'views/dialog_content.html',
                size: size,
                controller: 'ModalInstanceCtrl',
                resolve: {
                    items: function () {
                        return $scope.awesomeThings;
                    }
                }
            });

            modalInstance.result.then(function (selectedItem) {
                $scope.selected = selectedItem;
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        $scope.openLogin = function (size) {
            loginDialog.showLogin();
        };

        $scope.currentUsername = "Nothing";

        $scope.showUser = function () {
            $http.get('http://localhost:8081/angularJsPlay/rest/currentUser').then(function (response) {
                $scope.currentUsername = response.data.username;
            })
        };
        $scope.selectedProject = null;
        $scope.selectProject = function (project) {
            $scope.selectedProject = project;
            console.info(project);
        }
        $scope.listProjects = function () {
            $scope.projects = Projects.query();
            $scope.selectedProject = null;
        };

        $scope.backlogs = [];
        $scope.backlogCount = {value: 0};
        $scope.listBacklogs = function (updateCount, first, max) {
            if ($scope.selectedProject) {
                $scope.backlogs = Backlogs.forProject($scope.selectedProject.id,first, max);
                if (updateCount)
                    $scope.backlogCount = Backlogs.forProjectCount($scope.selectedProject.id);
            } else {
                $scope.backlogs = Backlogs.query({first: first, max: max});
                if (updateCount)
                    $scope.backlogCount = Backlogs.count();
            }

        }


    })
    .controller('BacklogsPaginationCtrl', function ($scope) {
        $scope.itemsPerPage = 5;
        $scope.currentPage = 1;
        $scope.max = 4;
        $scope.show = function () {
            return $scope.backlogCount.value > $scope.itemsPerPage;
        };
        $scope.$watch('currentPage', function (newPage, oldPage) {
            if (oldPage !== newPage) {
                var first = (newPage - 1) * $scope.itemsPerPage;
                $scope.listBacklogs(false, first, $scope.itemsPerPage);
            }
        });
    })
    .controller("ModalInstanceCtrl", function ($scope, $modalInstance, items, $log) {
        $scope.items = items;
        $scope.selected = {
            item: $scope.items[0]
        };
        $scope.getPatch = null; // for patchable directive
        $scope.user={
            description: 'Hello description',
            username:'Risa'
        }
        $scope.ok = function () {
            var patch = this.form.$getPatch();
            $log.info(patch);
            $modalInstance.close($scope.selected.item);
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    });
