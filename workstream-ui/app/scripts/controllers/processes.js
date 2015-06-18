angular.module('controllers.processes', ['resources.templates', 'resources.processes'])
    .controller('ProcessListController', ['$scope', '$stateParams', 'Processes', function ($scope, $stateParams, Processes) {
        $scope.stateObj = {
            isStartedByMeOpen: false,
            isInvolvesMeOpen: false
        }
        $scope.startedByMe = null;
        $scope.involvesMe = null;
        function loadStartedByMe() {
            $scope.startedByMe = Processes.getStartedByMe();
            ;
        }

        function loadInvolvesMe() {
            $scope.involvesMe = Processes.getInvolvesMe();
        }

        $scope.$watch('stateObj.isStartedByMeOpen', function (newValue, oldValue) {
            if (newValue && !$scope.startedByMe) {
                loadStartedByMe();
            }
        })
        $scope.$watch('stateObj.isInvolvesMeOpen', function (newValue, oldValue) {
            if (newValue && !$scope.involvesMe) {
                loadInvolvesMe();
            }
        })
        $scope.$on('state.reload', function () {
            if ($scope.startedByMe) {
                loadStartedByMe();
            }
            if ($scope.involvesMe) {
                loadInvolvesMe();
            }
        })
    }])
    .controller('ProcessDetailsController', ['$scope', '$stateParams', '$state', 'Processes', '$modal', 'Templates', '$filter',
        function ($scope, $stateParams, $state, Processes, $modal, Templates, $filter) {
            function getArchProcess(processId, archProcesses) {
                for (var i = 0; i < archProcesses.length; i++) {
                    var archProcess = archProcesses[i];
                    if (archProcess.id === processId) {
                        return archProcess;
                    }
                }
            }

            $scope.stateObj = {
                isTasksOpen: false,
                isDataOpen: false
            }
            function reloadProcess(onSuccess) {
                if (!onSuccess) {
                    onSuccess = function () {
                    };
                }
                $scope.archProcess = Processes.getArchive({processId: $stateParams.processId});
                $scope.process = Processes.get({processId: $stateParams.processId}, onSuccess, function (error) {
                    if (error.status === 404);
                    $scope.$emit('state.reload');
                    $state.go('^');
                });
            }

            reloadProcess();

            var random = null;
            $scope.getDiagramUrl = function (processId) {
                if (!processId) {
                    return null;
                }
                var url = Processes.getDiagramUrl(processId);
                if (random) {
                    url = url + '&random=' + random;
                }
                return url;
            }

            /** for tasks **/
            $scope.archTasks = null;
            function loadArchTasks() {
                $scope.archTasks = Processes.getArchiveTasks({processId: $scope.process.id});
            }

            $scope.$watch('stateObj.isTasksOpen', function (newValue, oldValue) {
                if (newValue && !$scope.archTasks) {
                    loadArchTasks();
                }
            })
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

            /** for data **/
            $scope.vars = null;
            $scope.varKeys = function () {
                if ($scope.vars) {
                    return Object.keys($scope.vars).filter(function (el) {
                        return el.indexOf('$') !== 0;
                    });
                }
            }
            function loadVars() {
                $scope.vars = Processes.getVars({processId: $scope.process.id});
            }

            $scope.formDef = {};
            function parseFormProps(bpmn) {
                var formProps = $(bpmn).find('activiti\\:formProperty');
                formProps.each(function(idx, formProp) {
                    var elem = $(formProp);
                    $scope.formDef[elem.attr('id')] = {
                        id: elem.attr('id'),
                        name: elem.attr('name'),
                        type: elem.attr('type')
                    }
                })
            }
            $scope.$watch('stateObj.isDataOpen', function (newValue, oldValue) {
                if (newValue && !$scope.vars) {
                    Templates.getBpmn({templateId: $scope.process.processDefinitionId}).then(function (response) {
                        // parse bpmn
                        parseFormProps(response.data);
                    });
                    loadVars();
                }
            })
            $scope.formatDataValue = function(key, value) {
                var def = $scope.formDef[key];
                if (!def) {
                    return value;
                }
                if (def.type === 'date') {
                    return $filter('date')(value, 'medium');
                } else {
                    return value;
                }
            }

            $scope.$on('tasks.complete', function (event, task) {
                reloadProcess(function () {
                    if ($scope.vars) {
                        loadVars();
                    }
                    if ($scope.archTasks) {
                        loadArchTasks();
                    }
                });
                random = Math.random();
            })
        }]);