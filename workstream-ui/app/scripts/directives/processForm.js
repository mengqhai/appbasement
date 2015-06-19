angular.module('directives.processForm', [])
    .directive('processForm', function() {
        var def = {
            restrict: 'E',
            templateUrl: 'views/processForm.html',
            scope: {
                formDef: '=',
                formObj: '='
            },
            link: function(scope, element, attrs) {
                if (!scope.formDef) {
                    throw 'form-def is not set';
                }
                function initForm(def) {
                    for (var i = 0; i < def.formProperties.length; i++) {
                        var field = def.formProperties[i];
                        var value;
                        switch (field.type.name) {
                            case 'boolean':
                                value = (field.value === 'true');
                                break;
                            default:
                                value = field.value;
                        }
                        scope.formObj[field.id] = value;
                    }
                }
                if (scope.formDef.$promise) {
                    scope.formDef.$promise.then(initForm)
                } else {
                    initForm(scope.formDef)
                }

                scope.stateObj = {};
                scope.dateInternal = {};
                scope.$watch('dateInternal', function(newValue, oldValue) {
                    Object.keys(newValue).forEach(function(key) {
                        if (newValue[key]) {
                            var date = newValue[key];
                            scope.formObj[key] = date.getFullYear()+'-'+(date.getMonth()+1)+'-'+(date.getDate()) + ' 00:00:00';
                        } else {
                            delete scope.formObj[key];
                            //scope.formObj[key] = null;
                        }
                    });
                }, true)
            }
        }
        return def;
    })