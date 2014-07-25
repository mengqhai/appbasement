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
                        ele.addClass('is-opened');
                        $document.bind('click', docClickHandler);
                    } else {
                        ele.removeClass('is-opened');
                        $document.unbind('click', docClickHandler);
                    }
                    console.info("doOpen:"+opened);
                };
                doOpen(popupInfo.isOpened);
                scope.$watch('popupInfo.isOpened', function (opened) {
                    doOpen(opened);
                });

                var docClickHandler = function (e) {
                    if(!popupInfo.isOpened) {
                        return;
                    }
//                    var tEle = angular.element(e.target);
//                    if (tEle.attr('toggle-popup') && tEle.attr('toggle-popup') === ele.attr('id')) {
//                        scope.toggle=tEle;
//                        return;
//                    }
//                    var popupParent = ele.parent()[0];
//                    var tParent = angular.element(e.target).parent()[0];
//                    if (tParent === popupParent) {
//                        return;
//                    }

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