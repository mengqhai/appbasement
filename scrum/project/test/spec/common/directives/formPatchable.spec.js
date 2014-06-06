describe('forPatchable', function () {
    var $scope, $compile;

    beforeEach(module('formPatchable'));
    beforeEach(inject(function ($rootScope, _$compile_) {
        $scope = $rootScope;
        $compile = _$compile_;
    }));

    it('should attach a $getPatch function to form controller', function () {
        var linkingFn = $compile("<form name='form' patchable>" +
            "<input name='username' ng-model='user.username'>" +
            "</form>")
        var element = linkingFn($scope);
        $scope.$digest();
        expect($scope.form).toBeDefined();
        expect($scope.form.$getPatch).toBeDefined();
    });

    describe('patch with dirty fields', function () {
        var element;
        beforeEach(function() {
            $scope.user = {
                username: 'notChanged',
                password: 'oldPassword'
            };
            var linkingFn = $compile("<form name='form' patchable>" +
                "<input id='username' name='username' ng-model='user.username'>" +
                "<input id='password' name='password' ng-model='user.password'>" +
                "<input id='repeat' name='_repeat' ng-model='user.repeat'>" +
                "</form>")
            element = linkingFn($scope);
            $scope.$digest();
        });


        it('should be able to create a patch with dirty fields', function () {
            var input = element.find('input'); // jqLite find is limited to find by tag name\
            expect(input.eq(0).val()).toEqual('notChanged');
            expect(input.eq(1).val()).toEqual('oldPassword');
            // input.val('changedName');   // <-- this won't work, see
            // http://stackoverflow.com/questions/15219717/to-test-a-custom-validation-angular-directive
            // must use NgModelController to manipulate values
            $scope.form.username.$setViewValue('changedName');
            expect($scope.user.username).toEqual('changedName');
            expect($scope.form.$dirty).toBeTruthy();

            $scope.form.password.$setViewValue('newPassword');
            var patch = $scope.form.$getPatch();
            expect(patch).toEqual({
                username: 'changedName',
                password: 'newPassword'
            });
        });

        it('should not patch non-dirty fields', function() {
            $scope.form.username.$setViewValue('changedName');
            // only change the username field
            var patch = $scope.form.$getPatch();
            expect(patch).toEqual({
                username: 'changedName'
            });
        });

        it('should not patch fields whose name starts with _', function() {
            $scope.form._repeat.$setViewValue('changedRepeat');
            var patch = $scope.form.$getPatch();
            expect(patch).toEqual({});
        });
    })


})