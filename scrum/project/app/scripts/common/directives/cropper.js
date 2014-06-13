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
                src: '=bindSrc'
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
                    img.attr('src', newValue);
                    img.Jcrop({
                        aspectRatio: scope.aspectRatio
                    }, function() {
                        jcrop_api = this;
                    });
                });

                scope.$on('$destroy', clear);
            }
        }
    });