'use strict';
describe('Service: UserService', function() {

  var UserService, httpBackend, http, q, data, usersURL, deleteURL, date, accountDetailsURL, id;

  beforeEach(module('hillromvestApp'));


  beforeEach(inject(function(_UserService_, $httpBackend, _$http_, _$q_) {
    UserService = _UserService_;
    http = _$http_;
    q = _$q_;
    httpBackend = $httpBackend;
    data = {
      'email': 'syedmohammadali+111@neevtech.com',
      'firstName': 'Syed',
      'lastName': 'Ali',
      'middleName': 'Mohammad',
      'role': 'SUPER_ADMIN',
      'title': 'Mr.'
    };
    id = 20;
    date = new Date('2015-01-01T00:00:00Z');
    spyOn(window, 'Date').and.callFake(function() {
      return date;
    });
    httpBackend.whenGET(/\.*.html/).respond({});
    httpBackend.whenGET(/\.*.json/).respond({});
    usersURL = 'api/hillromteamuser?cacheBuster=1420070400000';
    accountDetailsURL = 'api/account?cacheBuster=1420070400000';
    deleteURL = 'api/hillromteamuser/16?cacheBuster=1420070400000';
    httpBackend.whenPOST(usersURL).respond(200, data);
    httpBackend.whenGET(accountDetailsURL).respond(200, data);
    httpBackend.whenDELETE(deleteURL).respond(200, data);
  }));


  it('createUser', function() {
    var response = UserService.createUser(data);
    response.then(function(data) {
      httpBackend.flush();
      expect(response.status).toEqual(201);
    });
  });

  it('deleteUser', function() {
    var response = UserService.deleteUser(id);
    response.then(function(data){
      httpBackend.flush();
      expect(response.status).toEqual(200);
    });
  });
});
