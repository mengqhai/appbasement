angular.module('formPatchable', [])
    // if a field is not supposed to be in a patch, it's name should start with '_'
    .directive('patchable', function () {
        return {
            restrict: 'A',
            require: 'form',
            scope: false,
            link: function (scope, element, attrs, ctrl) {
                var getPatch = function () {
                    var patch = {};
                    for (fieldName in ctrl) {
                        if (fieldName.indexOf('$') === 0 || fieldName.indexOf('_') === 0)
                            continue;

                        var field = ctrl[fieldName];
                        if (ctrl[fieldName].$dirty)
                            patch[fieldName] = ctrl[fieldName].$modelValue;
                    };
                    return patch;
                };
                // added the function to the form controller
                if (attrs.patchable)
                    ctrl['$' + attrs.patchable] = getPatch;
                else
                    ctrl['$getPatch'] = getPatch;
            }
        };
    })