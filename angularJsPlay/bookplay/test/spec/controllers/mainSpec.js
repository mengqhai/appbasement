'use strict';

describe('Controller: HttpGreetingCtrl', function () {
    var scope, ctrl, mockBackend;
    beforeEach(module('bookplayApp'));
    beforeEach(inject(function (_$httpBackend_, $rootScope, $controller) {
        mockBackend = _$httpBackend_;
        mockBackend.expectGET("http://rest-service.guides.spring.io/greeting")
            .respond({"id": 1, "content": "Hello, World!"});
        scope = $rootScope.$new();
        ctrl = $controller('HttpGreetingCtrl', {$scope: scope});
    }));


    it('should get greeting from server on load', function () {
        // Initially, the request has not returned a response
        expect(scope.greeting).toBeUndefined();
        mockBackend.flush();

        expect(scope.greeting).toEqual({"id": 1, "content": "Hello, World!"});
    });
})

describe('Controller: MainCtrl', function () {

    // load the controller's module
    beforeEach(module('bookplayApp'));

    var MainCtrl,
        scope;

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, $rootScope) {
        scope = $rootScope.$new();
        MainCtrl = $controller('MainCtrl', {
            $scope: scope
        });
    }));

    it('should attach a list of awesomeThings to the scope', function () {
        expect(scope.awesomeThings.length).toBe(3);
    });
});
