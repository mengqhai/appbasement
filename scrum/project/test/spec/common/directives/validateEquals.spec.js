describe('validateEquals', function () {
    var $scope, form;

    function setTestValue(value) {
        $scope.model.testValue = value;
        $scope.$digest();
    };

    function setCompareTo(value) {
        $scope.model.compareTo = value;
        $scope.$digest();
    };

    beforeEach(module('validateEquals'));

    beforeEach(inject(function ($compile, $rootScope) {
        $scope = $rootScope;
        var element = angular.element('<form name="form">' +
            '<input name="testInput" ng-model="model.testValue" validate-equals="model.compareTo">' +
            '</form>');
        $scope.model = {
            testValue: '',
            compareTo: ''
        };
        $compile(element)($scope);
        $scope.$digest();
        expect($scope.form).toBeDefined();
        form = $scope.form;
    }));

    describe('model validity', function () {
        it('should be valid initially', function () {
            expect(form.testInput.$valid).toBe(true);
            expect(form.testInput.$error['equal']).toBe(false);
        });
        it('should be invalid if the model changes', function () {
            setTestValue('different');
            expect(form.testInput.$valid).toBe(false);
            expect(form.testInput.$error['equal']).toBe(true);
        });
        it('should be valid if the model changes to be the same as the reference', function() {
            setCompareTo('different');
            expect(form.testInput.$valid).toBe(false);
            expect(form.testInput.$error['equal']).toBe(true);

            setTestValue('different');
            expect(form.testInput.$valid).toBe(true);
            expect(form.testInput.$error['equal']).toBe(false);
        })
    });
});