angular.module('user-picker-demo',['components.user-picker'])
    .controller('DemoCtrl', function($scope) {
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
    });