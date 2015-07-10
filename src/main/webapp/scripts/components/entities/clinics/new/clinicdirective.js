'use strict';

angular.module('hillromvestApp')
  .directive('clinic', function(ClinicService) {
    return {
      templateUrl: 'scripts/components/entities/clinics/new/create.html',
      restrict: 'E',
      scope: {
        clinic: '=clinicData',
        isCreate:'=isCreate',
        isClinicCreated:'=isClinicCreated',
        isClinicDeleted:'=isClinicDeleted'
      },
      link: function postLink(scope, element, attrs) {},

      controller: function($scope) {
         $scope.createClinic = function () {
          console.log('Inside the Controller');
          var data = {
            'name': $scope.clinic.clinicName,
            'parentClinicName': $scope.clinic.satelliteName,
            'address': $scope.clinic.address,
            'zipcode': $scope.clinic.zip,
            'city': $scope.clinic.city,
            'state': $scope.clinic.state,
            'phoneNumber': $scope.clinic.phoneNumber,
            'faxNumber': $scope.clinic.faxNumber,
            'hillromId': null
          };
          console.log('data: ',data);

          ClinicService.createClinic(data).then(function (data) {
        	   console.log("Clinic got Created ");
             $scope.isClinicCreated = true;
        	  }).catch(function () {
              $scope.isClinicCreated = false;
        		  console.log("Clinic Creation failed ");
        	  });
        };
        
        $scope.deleteClinic = function(){
          $scope.clinic.id = 1;
          ClinicService.deleteClinic($scope.clinic.id).then(function (data) {
        		console.log("Clinic got deleted ");
              $scope.isClinicDeleted = true;
        	  }).catch(function () {

              $scope.isClinicDeleted = false;
        			console.log("Clinic deletion failed ");
        	  });
        	};
      }
    };
  });






