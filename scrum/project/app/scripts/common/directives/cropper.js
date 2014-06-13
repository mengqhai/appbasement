angular.module('cropper', [])
    // http://stackoverflow.com/questions/14319177/image-crop-with-angularjs
    // http://deepliquid.com/content/Jcrop.html
    .directive('cropper', function () {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: 'views/common/directives/cropper.tpl.html',
            scope: {
                aspectRatio: '@',
                src: '@',
                selected: '&'
            },
            link: function (scope, element, attrs) {

                var img = element.find('img').eq(0);
                img.attr('src', scope.src);
                img.Jcrop({
                    aspectRatio: scope.aspectRatio,
                    onSelect: scope.selected
                });
            }
        }
    });