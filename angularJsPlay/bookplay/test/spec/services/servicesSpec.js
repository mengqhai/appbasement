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

describe('Service: RestPromiseGreeting', function () {
    var mockBackend, loader, logger;

    beforeEach(function () {
        this.addMatchers({
            // we need to use toEqualData because the Resource hase extra properties
            // which make simple .toEqual not work.
            toEqualData: function (expect) {
                return angular.equals(expect, this.actual);
            }
        })
    });
    beforeEach(module('bookplayApp.services'));
    beforeEach(inject(function (_$httpBackend_, RestPromiseGreeting, $log) {
        mockBackend = _$httpBackend_;
        loader = RestPromiseGreeting;
        logger = $log;
    }));


    it('should load greeting', function () {
        mockBackend.expectGET('http://rest-service.guides.spring.io/greeting')
            .respond({id: 1, content: "Hello, World!"});

        var greeting;
        var promise = loader();
        logger.info(promise);
        promise.then(function (rec) {
            greeting = rec;
        });
        expect(greeting).toBeUndefined();
        mockBackend.flush();
        expect(greeting).toEqualData({id: 1, content: "Hello, World!"});
    });

});

describe('Service: Paginator', function () {
    beforeEach(module('bookplayApp.services'));

    var paginator;
    var items = [1, 2, 3, 4, 5, 6];
    var fetchFn = function (offset, limit, callback) {
        callback(items.slice(offset, offset + limit));
    };

    beforeEach(inject(function (Paginator) {
        paginator = Paginator(fetchFn, 3);
    }));

    it('should show items on the first page', function () {
        expect(paginator.currentPageItems).toEqual([1, 2, 3]);
        expect(paginator.hasNext()).toBeTruthy();
        expect(paginator.hasPrevious()).toBeFalsy();
    });

    it('should go to the next page', function () {
        paginator.next();
        expect(paginator.currentPageItems).toEqual([4, 5, 6]);
        expect(paginator.hasNext()).toBeFalsy();
        expect(paginator.hasPrevious()).toBeTruthy();
    })

    it('should go to previous page', function () {
        paginator.next();
        expect(paginator.currentPageItems).toEqual([4, 5, 6]);
        paginator.previous();
        expect(paginator.currentPageItems).toEqual([1, 2, 3]);
    });

    it('should not go next from last page', function () {
        paginator.next();
        expect(paginator.currentPageItems).toEqual([4, 5, 6]);
        paginator.next();
        expect(paginator.currentPageItems).toEqual([4, 5, 6]);
    });

    it('should not go back from first page', function () {
        paginator.previous();
        expect(paginator.currentPageItems).toEqual([1, 2, 3]);
    });
});