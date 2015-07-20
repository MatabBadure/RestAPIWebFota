'use strict';

angular.module('hillromvestApp')
  .directive('clinicList', function () {
    return {
      templateUrl: 'scripts/components/entities/clinics/list/list.html',
      restrict: 'E',
      scope: {
        onSelect: '&'
      },
      link: function (scope) {
        var clinic = scope.clinic;
      },
      controller: function ($scope) {
        $scope.clinics = [];

        $scope.selectClinic = function (clinic) {
          $scope.clinic = clinic;
          $scope.onSelect({
            'clinic': clinic
          });
        };

        $scope.searchClinics = function () {
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
            "parent":"Razorfish",
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
      }
    };
  });