'use strict';
describe('Service: ClinicService', function () {

  var ClinicService;

  beforeEach(module('hillromvestApp'));


  beforeEach(inject(function (_ClinicService_) {
    ClinicService = _ClinicService_;
  }));


  it('createClinic', function () {
    var data = {
      'address' : 'Address',
      'admin' : 'Manipal',
      'city' : 'City',
      'faxNumber' : '3423434434',
      'hillromId' : null,
      'name' : 'Clinic Name',
      'parent' : 'Parent Clinic Name',
      'phoneNumber' : '1234567890',
      'state' : 'State',
      'type' : 'child',
      'zipcode' : '123456'
    };
    var response = ClinicService.createClinic(data);
    expect(response.status).toEqual(201);

  });

  it('updateClinic', function () {

  });

  it('deleteClinic', function () {

  });
});