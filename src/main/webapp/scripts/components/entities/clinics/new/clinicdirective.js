'use strict';

angular.module('hillromvestApp')
  .directive('clinic', function (ClinicService) {
    return {
      templateUrl: 'scripts/components/entities/clinics/new/create.html',
      restrict: 'E',
      scope: {
        clinic: '=clinicData',
        clinicStatus: '=clinicStatus'
      },
      controller: function ($scope) {
        $scope.init = function () {
          $scope.clinic = {};
          $scope.clinic.type = 'parent';
        };

        $scope.init();
        $scope.newChildClinic = function () {
          $scope.clinic.childClinics.push({
            name: ''
          });
        };

        $scope.removeChildClinic = function (idx) {
          $scope.clinic.childClinics.splice(idx, 1);
        };

        $scope.clinicFormSubmit = function () {
          if ($scope.form.$invalid) {
            return false;
          }
          if ($scope.clinicStatus.isCreate) {
            $scope.clinic.hillromId = null;
            var data = $scope.clinic;

            console.info(data);

            ClinicService.createClinic(data).then(function (data) {
              $scope.clinicStatus.isMessage = true;
              $scope.clinicStatus.message = "Clinic created successfully" + " with ID " + data.data.ParentClinic.id;
              $scope.init();
              $scope.form.$setPristine();
            }).catch(function (response) {
              if (response.data.message !== undefined) {
                $scope.clinicStatus.message = response.data.message;
              } else {
                $scope.clinicStatus.message = 'Error occurred! Please try again';
              }
              $scope.clinicStatus.isMessage = true;
            });
          } else {
            $scope.clinic.hillromId = null;
            var data = $scope.clinic;
            ClinicService.updateClinic(data).then(function (data) {
              $scope.clinicStatus.isMessage = true;
              $scope.clinicStatus.message = "Clinic updated successfully" + " with ID " + data.data.ParentClinic.id;
              $scope.init();
              $scope.form.$setPristine();
            }).catch(function (response) {
              if (response.data.message !== undefined) {
                $scope.clinicStatus.message = response.data.message;
              } else {
                $scope.clinicStatus.message = 'Error occurred! Please try again';
              }
              $scope.clinicStatus.isMessage = true;
            });
          }
        };

        $scope.deleteClinic = function () {
          $scope.clinic.id = 1;
          ClinicService.deleteClinic($scope.clinic.id).then(function (data) {
            $scope.clinicStatus.isMessage = true;
            $scope.clinicStatus.message = data.data.message;
          }).catch(function (response) {
            if (response.data.message !== undefined) {
              $scope.clinicStatus.message = data.data.message;
            } else {
              $scope.clinicStatus.message = 'Error occured! Please try again';
            }
            $scope.clinicStatus.isMessage = true;
          });
        };

        $scope.getParentClinic = function () {
          $scope.clinics = [{
            "id": 64,
            "name": "Hill Rom",
            "address": "Neev",
            "zipcode": 560042,
            "city": "Bangalore",
            "state": "Karnataka",
            "phoneNumber": 9740353872,
            "faxNumber": 9942354883,
            "hillromId": null,
            "parentClinic": null,
            "npiNumber": null,
            "users": [],
            "patients": [],
            "deleted": false,
            "childClinics": [{
              "id": 65,
              "name": "abc"
            }, {
              "id": 66,
              "name": "xyz"
            }, {
              "id": 67,
              "name": "pqrs"
            }, {
              "id": 68,
              "name": "qwerty"
            }]
          }, {
            "id": 69,
            "name": "Neevtech",
            "parent": "Razorfish",
            "address": "RazorFish",
            "zipcode": 560048,
            "city": "bangalore",
            "state": "karnataka",
            "phoneNumber": 9740932492,
            "faxNumber": null,
            "hillromId": null,
            "parentClinic": null,
            "npiNumber": null,
            "users": [],
            "patients": [],
            "deleted": false,
            "childClinics": [{
              "id": 70,
              "name": "12345678"
            }]
          }];
        };

        $scope.selectClinic = function (clinic) {
          $scope.clinic.parent = clinic.name;
          $scope.clinics = [];
        };

        $scope.removeParent = function () {
          $scope.clinic.parent = null;
        };
      }
    };
  });
