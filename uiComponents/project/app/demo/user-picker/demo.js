angular.module('user-picker-demo',['components.user-picker'])
    .controller('DemoCtrl', function($scope, $q, $timeout) {
        $scope.users = [{
            username: 'Riso',
            email:'riso@wprss.com'
        },{
            username: 'Micheal',
            email:'Micheal@ssheee.com'
        },{
            username: 'Sally',
            email:'sally_2009@gmail.com'
        },
        {
            username: 'mqhnow1@sina.com',
            email:'mqhnow1@sina.com'
        },
            {
                username: 'Qinghai',
                email:'mengqhai@gmail.com'
            }
            ,{
                username: 'Lily Low',
                email:'lily_low@sun.com'
            }

        ];

        $scope.unassigned = {username:'Unassigned', email:'Unassigned'};

        $scope.selected = $scope.users[0];
        $scope.select = function(item) {
            $scope.selected = item;
        }



        $scope.filterUsers = function(user) {
            if (!$scope.query) {
                return true;
            }
            return (user.username.toLowerCase().indexOf($scope.query.toLowerCase()) !== -1 ||
                user.email.toLowerCase().indexOf($scope.query.toLowerCase())!== -1);
        }

        $scope.sInfo = {
            selected: null
        };

        $scope.commitSuccess = true;

        $scope.commitUser = function(selectedInfo) {
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
        $scope.commitUserWithMsg=function(selectedInfo) {
            resetMsg();
            return $scope.commitUser(selectedInfo).then(function(msg){
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