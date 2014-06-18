angular.module('directives.crud.buttons', [])
    .directive('crudButtons', function() {
        return {
            restrict: 'E',
            replace: true,
            template:'<div>' +
                '<button type="button" class="btn btn-primary" ng-disabled="!canSave()" ng-click="save()">Save</button> ' +
                '<button type="button" class="btn btn-warning" ng-disabled="!canRevert()" ng-click="revertChanges()">Revert changes</button> '+
                '<button type="button" class="btn btn-danger" ng-disabled="!canDelete()" ng-click="delete()">Delete</button>'+
                '</div>'
        }
    });