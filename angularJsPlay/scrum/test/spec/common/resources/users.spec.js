describe('Resource: Users', function () {
    var users, mockBackend, baseUrl, actualUserList;
    beforeEach(module('scrum.resources.users'));

    beforeEach(inject(function (_$httpBackend_, Users, SCRUM_CONFIG) {
        mockBackend = _$httpBackend_;
        users = Users;
        baseUrl = SCRUM_CONFIG.baseUrl;
        actualUserList = [
            {"id": 5, "username": "Risa", "createdAt": 1406352133000, "email": "ultrices.sit@et.co.uk"},
            {"id": 4, "username": "Inez", "createdAt": 1360821615000, "email": "odio.Nam@pede.org"},
            {"id": 3, "username": "Walker", "createdAt": 1362133066000, "email": "ac.mattis@ut.com"},
            {"id": 2, "username": "Sylvester", "createdAt": 1406100504000, "email": "ipsum@sollicitudin.org"}
        ];
    }));

    afterEach(function () {
        mockBackend.verifyNoOutstandingExpectation();
        mockBackend.verifyNoOutstandingRequest();
    })

//    beforeEach(function() {
//        this.addMatchers({
//            // we need to use toEqualData because the Resource has extra properties
//            // which make simple .toEqual not work.
//            toEqualData: function(expect) {
//                return angular.equals(expect, this.actual);
//            }
//        })
//    });

    it('should fetched list of users', function () {
        mockBackend.expectGET(baseUrl + 'user').respond(actualUserList);
        var userArray = users.query({});
        expect(userArray.length).toEqual(0);
        mockBackend.flush();
        expect(angular.equals(actualUserList, userArray)).toBeTruthy();
    });

    it('should fetch single user', function () {
        mockBackend.expectGET(baseUrl + 'user/5').respond([actualUserList[0]]);
        var userArray = users.query({id: 5});
        mockBackend.flush();
        expect(userArray.length).toEqual(1);
        var user = userArray[0];
        expect(user, actualUserList[0]).toBeTruthy();
        expect(user.getFullName()).toEqual('Risa (ultrices.sit@et.co.uk)');
    })
});