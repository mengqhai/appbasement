angular.module('directives.showmore', [])
    .directive('showmore', function() {
        return {
            restrict: 'E',
            scope: false,
            template: '<a href class="list-group-item list-group-item-info"><b>Show more</b></a>'
        }
    })