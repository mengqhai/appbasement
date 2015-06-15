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
            }
        }
        return def;
    })