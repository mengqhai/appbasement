describe('isUsernameUnique', function () {
    var Users, $scope, form, $timeout;

    function setTestValue(value) {
        $scope.model.testValue = value;
        $scope.$digest();
    };

    beforeEach(function () {
        Users = jasmine.createSpyObj('Users', ['isUsernameUnique']);
        Users.isUsernameUnique.andCallFake(function (viewValue, cb) {
            var result = {value: (viewValue !== 'mqhnow1') };
            cb(result);
        });

        // Mock Users resource
        angular.module('mock-Users', []).factory('Users', function () {
            return Users;
        });
    });

    beforeEach(module('uniqueChecks', 'mock-Users'));

    beforeEach(inject(function($compile, $rootScope, _$timeout_) {
        $scope = $rootScope;
        var element = angular.element('<form name="form"><input name="testInput" ng-model="model.testValue" unique-username></form>');
        $scope.model = {testValue: 'initialValue'};
        $compile(element)($scope);
        $scope.$digest();
        form = $scope.form;
        $timeout = _$timeout_;
    }));

    it('should have a working Users mock', inject(function (Users) {
        var cb = jasmine.createSpy();
        Users.isUsernameUnique('hello', cb);
        expect(cb).toHaveBeenCalledWith({value: true});
        cb.reset();
        Users.isUsernameUnique('mqhnow1', cb);
        expect(cb).toHaveBeenCalledWith({value: false});
    }));

    it('should be valid initially', function() {
        expect(form.testInput.$valid).toBe(true);
    });

    it('should not call Users.isUsernameUnique when the model changes', function() {
        setTestValue('different');
        expect($timeout.flush).toThrow('No deferred tasks to be flushed');
        expect(Users.isUsernameUnique).not.toHaveBeenCalled();
    });

    it('should not call Users.isUsernameUnique unless timed out', function() {
        form.testInput.$setViewValue('different');
        expect(Users.isUsernameUnique).not.toHaveBeenCalled();
        form.testInput.$setViewValue('different1');
        form.testInput.$setViewValue('different2');
        expect(Users.isUsernameUnique).not.toHaveBeenCalled();
        $timeout.flush();
        expect(Users.isUsernameUnique).toHaveBeenCalled();
        expect(Users.isUsernameUnique.calls.length).toEqual(1); // called only for once
        expect(form.testInput.$valid).toBe(true);
    });

    it('should call Users.isUsernameUnique when the view changes', function() {
        form.testInput.$setViewValue('different');
        $timeout.flush();
        expect(Users.isUsernameUnique).toHaveBeenCalled();
        expect(form.testInput.$valid).toBe(true);
    });

    it('should set model to invalid if username is not unique', function() {
        form.testInput.$setViewValue('mqhnow1');
        $timeout.flush();
        expect(Users.isUsernameUnique).toHaveBeenCalled();
        expect(form.testInput.$valid).toBe(false);
    });

    it('should set model to valid if it is set to the original value', function() {
        form.testInput.$setViewValue('mqhnow1');
        $timeout.flush();
        expect(form.testInput.$valid).toBe(false);
        form.testInput.$setViewValue('initialValue');
        expect($timeout.flush).toThrow('No deferred tasks to be flushed');
        expect(form.testInput.$valid).toBe(true);
    })
});