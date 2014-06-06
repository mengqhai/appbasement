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
                    angular.forEach(form, function (input) {
                        if (ctrl[input.name].$dirty && input.name.indexOf('_') !== 0) {
                            if (ctrl[input.name] && ctrl[input.name].$modelValue !== undefined) {
                                patch[input.name] = ctrl[input.name].$modelValue;
                            } else {
                                patch[input.name] = input.value;
                            }
                        }
                    });
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