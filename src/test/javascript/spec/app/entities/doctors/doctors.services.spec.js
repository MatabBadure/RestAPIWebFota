'use strict';
describe('Service: Doctor', function () {

  var Doctor;

  beforeEach(module('hillromvestApp'));


  beforeEach(inject(function (_Doctor_) {
    Doctor = _Doctor_;
  }));


  it('createDoctor', function () {
    var data = {
    };
    var response = Doctor.createDoctor(data);


  });

  it('deleteDoctor', function () {

  });
});