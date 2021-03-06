angular.module('cropper', [])
    // http://stackoverflow.com/questions/14319177/image-crop-with-angularjs
    // http://deepliquid.com/content/Jcrop.html
    .directive('cropper', function ($timeout) {
        return {
            restrict: 'E',
            replace: true,
            template: '<img>',
            scope: {
                aspectRatio: '@',
                src: '=bindSrc',
                boxHeight: '@',
                boxWidth: '@',
                coords: '=bindCoords',
                // TODO ? The bindCoords is now one-way binding (read only)
                // TODO ? consider to use ngModel to make it two-way: setting the coords model can change the select
                // TODO ? or maybe use scope.$on event to achieve that
                maxSelectWidth:'@',
                maxSelectHeight:'@',
                commitOnRelease: "@",
                initSelect: '&' // initial selection [x,y,x2,y2]
            },
            link: function (scope, element, attrs) {
                var img = element.eq(0);
                var jcrop_api;
                var clear = function () {
                    if (jcrop_api) {
                        jcrop_api.destroy();
                    }
                    ;
                };
                var refreshCoords = function (coords) {
                    $timeout(function() {
                        angular.copy(coords, scope.coords);
                    });
                    // $timeout service will call scope.$apply() each time
                    // a timer fires
//                    $timeout(function() {
//                        scope.$apply(function () {
//                            angular.copy(coords, scope.coords);
//                        });
//                    });
                };

                scope.$watch('src', function (newValue) {
                    clear();
                    img.removeAttr('style') // reset the width & height Jcrop has set
                    img.attr('src', newValue);
                    var config = {
                        aspectRatio: scope.aspectRatio,
                        boxHeight: scope.boxHeight,
                        boxWidth: scope.boxWidth,
                        onSelect: refreshCoords,
                        maxSize:[scope.maxSelectWidth, scope.maxSelectHeight]
                    };
                    if (!jcrop_api) {
                        config.setSelect = scope.initSelect();
                    };

                    if (!scope.commitOnRelease) {
                        config.onChange = refreshCoords;
                    }

                    img.Jcrop(config, function () {
                        jcrop_api = this;

                    });
                });

                scope.$on('$destroy', clear);
            }
        }
    })
    .directive('cropperPreview', function () {
        return {
            restrict: 'E',
            replace: true,
            template: '<div>' +
                '<img>' +
                '</div>',
            scope: {
                src: '=bindSrc',
                boxHeight: '@',
                boxWidth: '@',
                coords: '=bindCoords'
            },
            link: function (scope, element, attrs) {
                scope.boxWidth = scope.boxWidth || 100;
                scope.boxHeight = scope.boxHeight || 100;


                var div = element.eq(0);
                div.css({
                    width: scope.boxWidth + 'px',
                    height: scope.boxHeight + 'px',
                    overflow: 'hidden',
                    'margin-left': '5px'
                });
                var img = element.find('img').eq(0);
                var originSize;
                var showPreview = function (coords) {
                    if (!originSize) {
                        return;
                    }
                    var rx = div.width() / coords.w;
                    var ry = div.height() / coords.h;
                    img.css({
                        width: Math.round(originSize.width * rx),
                        height: Math.round(originSize.height * ry),
                        marginLeft: '-' + Math.round(rx * coords.x) + 'px',
                        marginTop: '-' + Math.round(ry * coords.y) + 'px'
                    }).show();
                };
                scope.$watch('src', function (newValue) {
                    img.removeAttr('style');
                    img.attr('src', newValue);
                    img.load(function () {
                        originSize = {
                            width: img.width(),
                            height: img.height()
                        };// must do this after image is loaded
                        img.hide();
                    });
                });

                scope.$watch('coords', function (newCoords) {
                    showPreview(newCoords);
                }, true);
            }
        }
    });