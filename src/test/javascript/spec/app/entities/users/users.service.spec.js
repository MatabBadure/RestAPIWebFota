'use strict';
describe('Service: User', function () {

  var User, httpBackend, http, q;

  beforeEach(module('hillromvestApp'));


  beforeEach(inject(function (_User_, $httpBackend,  _$http_, _$q_) {
    User = _User_;
    http = _$http_;
    q = _$q_;
    httpBackend = $httpBackend;
  }));


  it('createUser', function () {
    var data = {
      'email' : 'syedmohammadali@neevtech.com',
      'firstName' : 'Syed',
      'lastName' : 'Ali',
      'middleName' : 'Mohammad',
      'role' : 'SUPER_ADMIN',
      'title' : 'Mr.'
    };
    var response = User.createUser(data);
    httpBackend.flush();
    expect(response.status).toEqual(201);
  });

  it('deleteUser', function () {
    var id = '';
    var response = User.deleteUser(id);
    httpBackend.flush();
    expect(response.status).toEqual(200);
  });
});