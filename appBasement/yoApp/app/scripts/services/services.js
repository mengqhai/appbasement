/**
 * Created with JetBrains WebStorm.
 * User: liuli
 * Date: 14-3-1
 * Time: 下午5:38
 * To change this template use File | Settings | File Templates.
 */
var services = angular.module("guthub.services", ['ngResource']);

// Recipe Service
services.factory('Recipe', ['$resource', function ($resource) {
    return $resource('/recipes/:id', {id: "@id"});
}]);

// MultipleRecipeLoader Service
services.factory('MultipleRecipeLoader', ['Recipe', '$q', function (Recipe, $q) {
    return function () {
        var delay = $q.defer();
        Recipe.query(function (recipes) {
            delay.resolve(recipes);
        }, function () {
            delay.reject('Unable to fetch recipes');
        });
        return delay.promise;
    };
}]);

// RecipeLoader Service
services.factory('RecipeLoader', ['Recipe', '$route', '$q', function (Recipe, $route, $q) {
    return function () {
        var delay = $q.defer();
        Recipe.get({id: $route.current.params.recipeId}, function (recipe) {
            delay.resolve(recipe);
        }, function () {
            delay.reject('Unable to fetch recipe ' + $route.current.params.recipeId);
        });
        return delay.promise;
    }
}]);