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
            this.addExpander = function(expander) {
                expanders.push(expander);
            }
        }
    };
    return directiveDefinitionObject;
});