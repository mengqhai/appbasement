$(function() {
    var cropper = $('#nativeCropper');
    var showPreview = function(coords) {
        var rx = 100/coords.w;
        var ry = 100/coords.h;


        $('#nativePreview').css({
            width: Math.round(cropper.width() * rx),
            height: Math.round(cropper.height()* ry),
            marginLeft: '-'+Math.round(rx*coords.x)+'px',
            marginTop:'-'+Math.round(ry*coords.y)+'px'
        }).show();
    };

    cropper.Jcrop({
        onChange: showPreview,
        onSelect: showPreview,
        aspectRatio: 1
    });
});
angular.module('cropperPlay', ['cropper'])
    .controller('cropperPlayCtrl', function($scope) {
        $scope.imgSrc="images/motor.jpg";

        $scope.showPreview = function(coords) {
            var rx = 100/coords.w;
            var ry = 100/coords.h;


            $('#nativePreview').css({
                width: Math.round(cropper.width() * rx),
                height: Math.round(cropper.height()* ry),
                marginLeft: '-'+Math.round(rx*coords.x)+'px',
                marginTop:'-'+Math.round(ry*coords.y)+'px'
            }).show();
        };
    });
