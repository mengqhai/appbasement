angular.module('components.dropdown-popup', [])
    .directive('dropdownPopup', function ($document) {
        var def = {
            restrict: 'AE',
            replace: true,
            transclude: true,
            template: '<div class="dropdown-popup">' +
                '<div ng-transclude></div>' +
                '</div>',
            scope: {
                popupInfo: '='
            },
            link: function (scope, ele, attrs) {
                if (!angular.isDefined(scope.popupInfo)) {
                    scope.popupInfo = {
                        isOpened: false
                    };
                }
                var popupInfo = scope.popupInfo;

                var doOpen = function (opened) {
                    if (opened) {
                        besideToggle(scope.toggle);
                        ele.addClass('is-opened');
                        $document.bind('click', docClickHandler);
                    } else {
                        ele.removeClass('is-opened');
                        $document.unbind('click', docClickHandler);
                    }
                }
                var besideToggle = function(tEle) {
                    if (!tEle) {
                        return;
                    }
                    var th = tEle.css('height');
                    var tw = tEle.css('width');
                    console.info(th+" "+tw);
                }
                doOpen(popupInfo.isOpened);
                scope.$watch('popupInfo.isOpened', function (opened) {
                    doOpen(opened);
                });

                var docClickHandler = function (e) {
                    if(!popupInfo.isOpened) {
                        return;
                    }
                    var tEle = angular.element(e.target);
                    if (tEle.attr('toggle-popup') === ele.attr('id')) {
                        scope.toggle=tEle;
                        return;
                    }
                    scope.$apply(function () {
                        popupInfo.isOpened = false;
                    })
                };

                ele.on('click', function (e) {
                    e.stopPropagation();
                });
            }
        }
        return def;
    });