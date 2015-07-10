'use strict';

angular.module('hillromvestApp')
  .directive('clinic', function() {
    return {
      templateUrl: 'scripts/components/entities/clinics/new/create.html',
      restrict: 'E',
      scope: {
        clinic: '=clinicData'
      },
      link: function postLink(scope, element, attrs) {},

      controller: function($scope) {
         $scope.createClinic = function () {
          console.log('Inside the Controller');
          var data = {
            'clinicName': $scope.clinic.clinicName,
            'satelliteClinicName': $scope.clinic.satelliteName,
            'address': $scope.clinic.address,
            'zip': $scope.clinic.zip,
            'city': $scope.clinic.city,
            'state': $scope.clinic.state,
            'phoneNumber': $scope.clinic.phoneNumber,
            'faxNumber': $scope.clinic.faxNumber,
            'admin': $scope.clinic.admin

          };
         
          console.log('data: ',data);
        };
      }
    };
  });

