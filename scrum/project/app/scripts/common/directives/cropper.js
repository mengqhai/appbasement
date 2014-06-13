angular.module('cropper', [])
    // http://stackoverflow.com/questions/14319177/image-crop-with-angularjs
    // http://deepliquid.com/content/Jcrop.html
    .directive('cropper', function () {
        return {
            restrict: 'E',
            replace: true,
            template:'<img>',
            scope: {
                aspectRatio: '@',
                src: '=bindSrc',
                boxHeight:'@',
                boxWidth:'@'
            },
            link: function (scope, element, attrs) {
                var img = element.eq(0);



                var jcrop_api;
                var clear = function() {
                    if (jcrop_api) {
                        jcrop_api.destroy();
                    };
                };
                scope.$watch('src', function(newValue) {
                    clear();
                    img.removeAttr('style') // reset the width & height Jcrop has set
                    img.attr('src', newValue);
                    console.info(img.width()+" "+img.height());
                    img.Jcrop({
                        aspectRatio: scope.aspectRatio,
                        boxHeight: scope.boxHeight,
                        boxWidth: scope.boxWidth
                    }, function() {
                        jcrop_api = this;
                    });
                });

                scope.$on('$destroy', clear);
            }
        }
    });