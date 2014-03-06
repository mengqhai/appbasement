/**
 * Created with JetBrains WebStorm.
 * User: qinghai
 * Date: 14-3-4
 * Time: 下午3:06
 * To change this template use File | Settings | File Templates.
 */
var directives = angular.module('bookplayApp.directives', []);
directives.directive('hello', function () {

    return {
        restrict: 'E',
        transclude: true,
        template: '<div>Hi there <span ng-transclude></span></div>',
        replace: true
    };
});

directives.directive('expander', function factory() {
    var directiveDefinitionObject = {
        restrict: "EA",
        replace: true,
        transclude: true,
        require: "^?accordion",
        scope: {title: '=expanderTitle'},
        templateUrl: 'views/expanderTemplate.html',
        link: function (scope, element, attrs, accordionController) {
            scope.showMe = false;
            if (accordionController)
                accordionController.addExpander(scope);

            scope.toggle = function toggle() {
                scope.showMe = !scope.showMe;
                if (accordionController)
                    accordionController.gotOpened(scope);
            }
        }
    };
    return directiveDefinitionObject;
});

directives.directive('accordion', function () {
    var directiveDefinitionObject = {
        restrict: 'EA',
        replace: true,
        transclude: true,
        template: '<div ng-transclude></div>',
        controller: function aCtrl() {
            var expanders = [];
            this.gotOpened = function (selectedExpander) {
                angular.forEach(expanders, function (expander) {
                    if (selectedExpander != expander) {
                        expander.showMe = false;
                    }
                })
            };
            this.addExpander = function (expander) {
                expanders.push(expander);
            }
        }
    };
    return directiveDefinitionObject;
});

directives.directive('datepicker', function () {

    return {
        restrict: 'A',
        require: '?ngModel',
        scope: {
            select: '&'
        },
        link: function (scope, element, attrs, ngModelCtrl) {
            if (!ngModelCtrl) return;
            var optionsObj = {};
            optionsObj.dateFormat = 'mm/dd/yy';

            var updateModel = function (dateTxt) {
                scope.$apply(function () {
                    // see http://docs.angularjs.org/api/ng/type/ngModel.NgModelController
                    ngModelCtrl.$setViewValue(dateTxt);
                });
            }
            optionsObj.onSelect = function (dateTxt, picker) {
                updateModel(dateTxt);
                if (scope.select) {
                    scope.$apply(function () {
                        scope.select({date: dateTxt})
                    });
                }

            };

            // Called when the view needs to be updated. It is expected that the user of the ng-model
            // directive will implement this method.
            ngModelCtrl.$render = function () {
                element.datepicker('setDate', ngModelCtrl.$viewValue || '');
            };
            element.datepicker(optionsObj);
        }
    }
});




















