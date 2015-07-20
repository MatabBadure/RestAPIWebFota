'use strict';
describe('Service: User', function () {

  var User;

  beforeEach(module('hillromvestApp'));


  beforeEach(inject(function (_User_) {
    User = _User_;
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
    var response = User.createClinic(data);
    expect(response.status).toEqual(201);
  });

  it('deleteUser', function () {
    var id = '';
    var response = User.deleteUser(id);
    expect(response.status).toEqual(200);
  });
});