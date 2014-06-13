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
        aspectRatio: 1,
        boxHeight: 300,
        boxWidth: 300
    });
});
angular.module('cropperPlay', ['cropper','field'])
    .controller('cropperPlayCtrl', function($scope) {
        $scope.srcs = {dog:"http://pic25.nipic.com/20121121/668573_131030162115_2.jpg",
            motor:"http://127.0.0.1:9000/images/motor.jpg",
            smallDog: "http://b.hiphotos.baidu.com/zhidao/pic/item/4ec2d5628535e5dd5e3d5ef177c6a7efce1b628f.jpg"};
        $scope.imgInfo = {
            src:"http://pic25.nipic.com/20121121/668573_131030162115_2.jpg"
        };
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
