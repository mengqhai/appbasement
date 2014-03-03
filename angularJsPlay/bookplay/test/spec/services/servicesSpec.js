/**
 * Created with JetBrains WebStorm.
 * User: liuli
 * Date: 14-3-3
 * Time: 下午8:58
 * To change this template use File | Settings | File Templates.
 */
describe('Service: RestGreeting', function () {
    var mockBackend, loader;

    beforeEach(module('bookplayApp.services'));
    beforeEach(inject(function (_$httpBackend_, RestGreeting) {
        mockBackend = _$httpBackend_;
        loader = RestGreeting;
    }));

    it('should load greeting', function () {
        mockBackend.expectGET('http://rest-service.guides.spring.io/greeting')
            .respond({"id": 1, "content": "Hello, World!"});

        loader.get(function (greeting) {
            expect(greeting instanceof RestGreeting).toEqual(true);
            expect(greeting.id).toEqual(1);
            expect(greeting.content).toEqual("Hello, World!");
        });
    })
});

describe('Service: RestPromiseGreeting', function() {
    var mockBackend, loader, logger;

    beforeEach(function() {
        this.addMatchers({
            // we need to use toEqualData because the Resource hase extra properties
            // which make simple .toEqual not work.
            toEqualData: function(expect) {
                return angular.equals(expect, this.actual);
            }
        })
    });
    beforeEach(module('bookplayApp.services'));
    beforeEach(inject(function(_$httpBackend_, RestPromiseGreeting, $log){
        mockBackend = _$httpBackend_;
        loader = RestPromiseGreeting;
        logger = $log;
    }));


    it('should load greeting', function() {
        mockBackend.expectGET('http://rest-service.guides.spring.io/greeting')
            .respond({id:1, content:"Hello, World!"});

        var greeting;
        var promise = loader();
        logger.info(promise);
        promise.then(function(rec){
            greeting = rec;
        });
        expect(greeting).toBeUndefined();
        mockBackend.flush();
        expect(greeting).toEqualData({id:1, content:"Hello, World!"});
    });

});