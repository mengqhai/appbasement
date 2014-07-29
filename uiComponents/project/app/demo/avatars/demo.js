angular.module('avatar-demo', ['components.avatar'])
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
    })