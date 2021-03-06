angular.module('directives.crud.edit', [])
    // Apply this directive to an element at or below a form that will manage CRUD operations on a resource.
    // - The resource must expose the following instance methods: id, $save(), $query(), $get(), and $delete() (be a resource)
    .directive('crudEdit', ['$parse', function($parse) {
        return {
            // We ask this directive to create a new child scope so that when we add helper methods to the scope
            // it doesn't make a mess of the parent scope.
            // - Be aware that if you write to the scope from within the form then you must remember that there is a child scope at the point
            scope: true,
            // We need access to a form so we require a FormController from this element or a parent element
            require: '^form',
            restrict: 'A',
            link: function (scope, element, attrs, form) {
                // We extract the value of the crudEdit attribute
                // - it should be an assignable expression evaluating to the model (resource) that is going to be edited
                // The $parseservice, when called with an expression as its argument,
                // will return a getter function. The returned getter function will have the assign
                // property (a setter function) if the supplied AngularJS expression is assignable.
                var resourceGetter = $parse(attrs.crudEdit);
                var resourceSetter = resourceGetter.assign;
                // Store the resource object for easy access
                var resource = resourceGetter(scope);
                // Store a copy for reverting the changes
                var original = angular.copy(resource);
                var checkResourceMethod = function(methodName) {
                    if ( !angular.isFunction(resource[methodName]) ) {
                        throw new Error('crudEdit directive: The resource must expose the ' + methodName + '() instance method');
                    }
                };
                checkResourceMethod('$save');
                checkResourceMethod('$delete');
                checkResourceMethod('$get');
                checkResourceMethod('$update');



                // TODO callback functions
                // This function helps us extract the callback functions from the directive attributes
                var makeFn = function(attrName) {
                    var fun = scope.$eval(attrs[attrName]);
                    if (!angular.isFunction(fn)) {
                        throw new Error('crudEdit directive: The attribute "'+attrName+'" must evaluate to be a function');
                    }
                    return fn;
                };
                // Set up callbacks with fallback
                // onSave attribute -> onSave scope -> noop
                var userOnSave = attrs.onSave ? makeFun('onSave') : (scope.onSave || angular.noop);
                var onSave = function(result, status, headers, config) {
                    // Reset the original to help with reverting and pristine checks
                    original = result;
                    userOnSave(result, status, headers, config);
                };
                // onDelete attribute -> onRemove scope -> onSave attribute -> onSave scope -> noop
                var onDelete = attrs.onDelete ? makeFn('onDelete') : (scope.onDelete || angular.noop);
                // onError attribute -> onError scope -> noop
                var onError = attrs.onError ? makeFn('onError') : (scope.onError || angular.noop);


                // HTTP GET "class" actions: Resource.action([parameters], [success], [error])
                // non-GET "class" actions: Resource.action([parameters], postData, [success], [error])
                // non-GET instance actions: instance.$action([parameters], [success], [error])
                scope.create = function() {
                    resource.$save(onSave, onError);
                };

                scope.update = function() {
                    if (angular.isFunction(form['$getPatch'])) {
                        var patch = form.$getPatch();
                        var patchOnSave = function (result, status, headers, config) {
                            var patched = angular.extend(resource,patch);
                            onSave(patched,status, headers, config);
                        };
                        resource.$update(patch, patchOnSave, onError); // patchable support
                    } else {
                        resource.$update(onSave, onError);
                    }
                };

                if (resource.id!==undefined) {
                    scope.save = scope.update;
                } else {
                    scope.save = scope.create;
                };

                scope.revertChanges = function() {
                    resource = angular.copy(original);
                    resourceSetter(scope, resource);
                    form.$setPristine();
                };

                scope.delete = function() {
                    if (resource.id) {
                        resource.$delete(onDelete, onError);
                    } else {
                        onDelete();
                    }
                };

                // The following functions can be called to modify the behaviour of elements in the form
                // - e.g. ng-disable="!canSave()"
                scope.canSave = function() {
                    return form.$valid && form.$dirty;
                };
                scope.canRevert = function() {
                    return !angular.equals(resource, original);
                };
                scope.canDelete = function() {
                    return resource.id !== undefined;
                };
            } // end link

        };
    }]);