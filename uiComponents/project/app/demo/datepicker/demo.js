angular.module('datepicker-demo',['ui.bootstrap','ui.bootstrap.datepicker', 'components.datepicker-panel',
        'components.dropdown-popup', 'ui.bootstrap.buttons', 'ngAnimate'])
    .controller('DemoCtrl', function($scope, $q, $timeout) {

        $scope.date1 = new Date();
        $scope.dateInfo = {
            date: new Date(),
            urgent: false
        };
        $scope.popupInfo = {
            isOpened:false
        };

        $scope.$watch('dateInfo', function(newValue) {
            $scope.popupInfo.isOpened = false;
        },true);

        $scope.commitSuccess = true;

        $scope.commitDate = function(dateInfo) {
            var deferred = $q.defer();
            $timeout(function() {
                if ($scope.commitSuccess) {
                    deferred.resolve("Date info updated on server");
                } else {
                    deferred.reject("Server error");
                };
            }, 1000);

            return deferred.promise;
        };
        // a decorated function to update the message also
        $scope.commitDateWithMsg=function(dateInfo) {
            resetMsg();
            return $scope.commitDate(dateInfo).then(function(msg){
                $scope.successMsg=msg;
            },function(msg){
                $scope.errorMsg=msg;
                // rethrow (forward) the error to other chained failure callbacks
                return $q.reject(msg);
            })
        }
        var resetMsg = function() {
            $scope.successMsg="";
            $scope.errorMsg="";
        };
        resetMsg();
    });