// http://plnkr.co/edit/iiP0t8?p=info
// https://github.com/angular-ui/bootstrap/issues/590
angular.module('directives.popoverToggle', ['ui.bootstrap'])
    .config(function ($tooltipProvider) {
        $tooltipProvider.setTriggers({'open': 'close'});
    })
    .directive('popoverToggle', function () {
        return {
            scope: true,
            controller: function ($element, $timeout) {
                var ctrl = this;
                ctrl.toggle = function () {
                    $timeout(function () {
                        $element.triggerHandler(ctrl.openned ? 'close' : 'open');
                        ctrl.openned = !ctrl.openned;
                    });
                }
            },
            controllerAs: 'popoverCtrl',
            link: function (scope, element, attrs, popoverCtrl) {
                return element.on('click', popoverCtrl.toggle);
            }
        };
    });
