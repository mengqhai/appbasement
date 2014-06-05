angular.module('validateEquals',[])
/**
 * A validation directive to ensure that this model has the same value as some other
 */
    .directive('validateEquals', function() {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function(scope, elm, attrs, ngModelController) {
                var validate = function (myValue, otherValue) {
                    if (myValue === otherValue) {
                        // $setValidity(validationErrorKey, isValid) -- 'equal' is just a validation error key
                        ngModelController.$setValidity('equal',true);
                        return myValue;
                    } else {
                        ngModelController.$setValidity('equal',false);
                        return undefined;
                    }
                };

                // $watch(expression, handler) -- attrs.validateEquals is an expression, e.g. user.password, which refers
                // to model object $scope.user.password
                scope.$watch(attrs.validateEquals, function(otherModelValue) {
                    ngModelController.$setValidity('equal', ngModelController.$viewValue === otherModelValue);
                });

                ngModelController.$parsers.push(function(viewValue) {
                    // $scope.$eval([expression], [locals]); Executes the expression on the current scope and returns the result.
                    // Any exceptions in the expression are propagated (uncaught).
                    // This is useful when evaluating Angular expressions.
                    return validate(viewValue, scope.$eval(attrs.validateEquals));
                });

                ngModelController.$formatters.push(function(modelValue) {
                    return validate(modelValue, scope.$eval(attrs.validateEquals));
                });
            }
        };
    })