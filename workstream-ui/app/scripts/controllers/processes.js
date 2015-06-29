angular.module('controllers.processes', ['resources.templates', 'resources.processes'])
    .controller('ProcessListController', ['$scope', '$stateParams', 'Processes', function ($scope, $stateParams, Processes) {
        $scope.stateObj = {
            isStartedByMeOpen: false,
            isInvolvesMeOpen: false
        }
        $scope.startedByMe = null;
        $scope.involvesMe = null;


        $scope.status = $stateParams.status ? $stateParams.status : 'active';
        $scope.loader = Processes.createLoader($scope.status);
        var loader = $scope.loader;

        $scope.startedByMeCount = loader.countStartedByMe();
        $scope.involvesMeCount = loader.countInvolvesMe();

        function loadStartedByMe(onSuccess, first, max) {
            if (first === undefined || max === undefined) {
                first = 0;
                max = 10;
            }
            return loader.getStartedByMe({first: first, max: max}, onSuccess);
        }

        function loadInvolvesMe(onSuccess, first, max) {
            if (first === undefined || max === undefined) {
                first = 0;
                max = 10;
            }
            return loader.getInvolvesMe({first: first, max: max}, onSuccess);
        }
        function loadMore(loaderFun, theArray) {
            loaderFun(function(moreArray) {
                $.merge(theArray, moreArray);
            }, theArray.length, 10);
        }

        $scope.loadMoreStartedByMe = function () {
            loadMore(loadStartedByMe, $scope.startedByMe);
        }
        $scope.loadMoreInvolvesMe = function() {
            loadMore(loadInvolvesMe, $scope.involvesMe);
        }

        $scope.$watch('stateObj.isStartedByMeOpen', function (newValue, oldValue) {
            if (newValue && !$scope.startedByMe) {
                $scope.startedByMe = loadStartedByMe();
            }
        })
        $scope.$watch('stateObj.isInvolvesMeOpen', function (newValue, oldValue) {
            if (newValue && !$scope.involvesMe) {
                $scope.involvesMe =  loadInvolvesMe();
            }
        })
    }])
    .controller('ProcessDetailsController', ['$scope', '$stateParams', '$state', 'Processes', '$modal', 'Templates', '$filter',
        function ($scope, $stateParams, $state, Processes, $modal, Templates, $filter) {
            $scope.stateObj = {
                isTasksOpen: false,
                isDataOpen: false
            }

            var loader = $scope.loader; // from $parent
            function createLoader(archProcess) {
                if (archProcess.endTime && !loader) {
                    loader = Processes.createLoader('archived');
                } else {
                    loader = Processes.createLoader('active');
                };
            }
            $scope.archProcess = Processes.getArchive({processId: $stateParams.processId}, function(archProcess) {
                createLoader(archProcess);
                reloadProcess();
            });


            function reloadProcess(onSuccess) {
                if (!onSuccess) {
                    onSuccess = function () {
                    };
                }

//                if (!$scope.archProcess) {
//                    $scope.archProcess = loader.getArchive({processId: $stateParams.processId});
//                }

                if ($scope.archProcess.endTime) {
                    $scope.process = $scope.archProcess;
                } else {
                    $scope.process = loader.get({processId: $stateParams.processId}, onSuccess, function (error) {
                        $state.go('processes.details', {
                            status: 'archived',
                            processId: $stateParams.processId
                        })
                    });
                }
            }

            var random = null;
            $scope.getDiagramUrl = function (processId) {
                if (!processId) {
                    return null;
                }
                var url = loader.getDiagramUrl(processId);
                if (random) {
                    url = url + '&random=' + random;
                }
                return url;
            }

            /** for tasks **/
            $scope.archTasks = null;
            function loadArchTasks() {
                $scope.archTasks = loader.getArchiveTasks({processId: $scope.process.id});
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
                $scope.vars = loader.getVars({processId: $scope.process.id});
            }

            $scope.formDef = {};
            function parseFormProps(bpmn) {
                var formProps = $(bpmn).find('activiti\\:formProperty');
                formProps.each(function (idx, formProp) {
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
            $scope.formatDataValue = function (key, value) {
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

            /*
             for archived process, process.processDefinitionName === undefined, so need to get the name
             from REST API
             */
            var template = null;
            $scope.getProcessTemplate = function (templateId) {
                if (template != null) {
                    return template;
                } else {
                    template = Templates.get({templateId: templateId});
                    return template;
                }
            }
        }]);