angular.module('field', [])
    // Usage:
    // <field type="email" ng-model="user.email" required>
    //   <label>Email</label>
    //   <validator key="required">{{$fieldLabel}} is required</validator>
    //   <validator key="email">Please enter a valid email</validator>
    // </field>
    .directive('field', function ($interpolate, $http, $templateCache, $compile) {
        var getValidationMessageMaps = function (element) {
            // For each of the <validator>elements we use the $interpolateservice create an
            // interpolation function from the text of the element and add that function into a map
            // based on the value of the validator element's key attribute.
            var messageFns = {};
            var validators = element.find('validator');
            angular.forEach(validators, function (validator) {
                validator = angular.element(validator);
                var iFn = $interpolate(validator.text());
                messageFns[validator.attr('key')] = function(theScope) {
                    return iFn(theScope);
                };
                //  If we pass such a string to the $interpolate service, it returns an
                // interpolation function that takes a scope and returns the interpolated string:
            });

            // This map will be added
            // to the template's scope as $validationMessages.
            // To display the validation error messages in our field templates, we will have
            // something like this:
            // <span class="help-inline" ng-repeat="error in $fieldErrors">
            //   {{$validationMessages[error](this)}}
            // </span>
            // The $fieldErrors property contains a list of the current invalid validation error
            // keys. It is updated by a watch created in the success handler for loadTemplate().
            // see setUpTemplate()
            return messageFns;
        };
        var getLabelContent = function (element) {
            var label = element.find('label');
            return label[0] && label.html();
        };
        var loadTemplate = function (templateUrl) {
            return $http.get(templateUrl, {cache: $templateCache})
                .then(function (response) {
                    return angular.element(response.data);
                }, function (response) {
                    throw new Error('Template not found: ' + templateUrl);
                })
        };

        var findInputElement = function(element) {
            return angular.element(
                element.find('input')[0] ||
                element.find('textarea')[0] ||
                element.find('select')[0]
            );
        };


        var setUpChildScope = function(scope, validationMsgs, labelContent, fieldAttrs) {
            // set up the scope of the template
            // we will attach useful properties to childScope, such as
            // $validationMessages, $fieldId, $fieldLabel, and $fieldErrors:
            var childScope = scope.$new();
            childScope.$validationMessages = angular.copy(validationMsgs);

            // Generate an id for the input from the ng-model expression
            // (we need to replace dots with something to work with browsers and also form scope)
            // (We couldn't do this in the compile function as we need the scope to
            // be able to calculate the unique id)
            childScope.$fieldId = fieldAttrs.ngModel.replace('.', '_').toLowerCase()+'_'+childScope.$id;
            childScope.$fieldLabel = labelContent;
            childScope.$watch('$field.$dirty && $field.$error', function() {
                // errorList = new value of expression ($field.$dirty && $field.$error) = $field.$error if $field.$dirty
                var errorList = childScope.$field.$error;
                childScope.$fieldErrors = [];
                angular.forEach(errorList, function(invalid, key) {
                    if (invalid) {
                        childScope.$fieldErrors.push(key);
                    }
                });
            }, true);

            return childScope;
        };

        var setUpTemplate = function(childScope, fieldElement, templateElement, fieldAttrs) {
            // We copy over all the attributes from the field directive's element to the template's
            // input element and add on computed values for the nameand idattributes
            var inputElement = findInputElement(templateElement);
            angular.forEach(fieldAttrs.$attr, function(original, normalized) {
                // angular.forEach function, orignal = value, normalized = key
                var value = fieldElement.attr(original);
                inputElement.attr(original, value);
            });
            if (!inputElement.attr('name')) {
                inputElement.attr('name', childScope.$fieldId);
            }
            inputElement.attr('id', childScope.$fieldId);

            // label set up
            var labelElement = templateElement.find('label');
            labelElement.attr('for', childScope.$fieldId);
            labelElement.html(childScope.$fieldLabel);

            // We append the templateElementto the original fieldelement, then use the
            // $compileservice to compile and link it to our new childScope. Once the element is
            // linked, the ngModelControlleris available for us to put into the $fieldproperty
            // for the template to use.
            fieldElement.append(templateElement);
            $compile(templateElement)(childScope);
            childScope.$field = inputElement.controller('ngModel');
        };


        return {
            restrict: 'E',
            priority: 100, // We need this directive to happen before ng-model
            terminal: true, // We are going to deal with this element
            // Once we have terminated the compilation we can modify the directive's element
            // and its children, but then we are responsible for setting up any new scopes, correctly
            // transcluding content and also for further compiling of child elements that may
            // contain directives.
            require: ['?^form'], // If we are in a form then we can access the ngModelController
            compile: function (element, attrs) {
                if (!attrs['ngModel']) {
                    throw new Error('ng-model not set for field '+attrs.name);
                }

                var validationMsgs = getValidationMessageMaps(element);
                var labelContent = getLabelContent(element);
                element.html('');  // we emtpy out the content of the element so that we have a clean
                // element in which to load the template

                return function postLink(scope, element, attrs) {
                    var templateType = attrs.template || 'input';
                    var templateUrl = 'views/common/directives/field/'+templateType+'.tpl.html';
                    loadTemplate(templateUrl).then(function (templateElement) {
                        var childScope = setUpChildScope(scope, validationMsgs, labelContent, attrs);
                        setUpTemplate(childScope, element, templateElement, attrs);
                    });
                };
            }
        }
    });