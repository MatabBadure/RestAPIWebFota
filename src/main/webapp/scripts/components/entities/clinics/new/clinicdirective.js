  'use strict';

  angular.module('hillromvestApp')
  .directive('clinic', function(ClinicService) {
        return {
          templateUrl: 'scripts/components/entities/clinics/new/create.html',
          restrict: 'E',
          scope: {
            clinic: '=clinicData',
            clinicStatus: '=clinicStatus'
          },
         

          controller: function($scope) {
           $scope.createClinic = function () {
            if($scope.form.$invalid){
              return false;
            }
            var data = {
              'name': $scope.clinic.name,
              'parentClinicName': $scope.clinic.parentClinicName,
              'address': $scope.clinic.address,
              'zipcode': $scope.clinic.zipcode,
              'city': $scope.clinic.city,
              'state': $scope.clinic.state,
              'phoneNumber': $scope.clinic.phoneNumber,
              'faxNumber': $scope.clinic.faxNumber,
              'hillromId': null
            };

            ClinicService.createClinic(data).then(function (data) {    	 
                 $scope.clinicStatus.isMessage = true;
                 $scope.clinicStatus.message = "Clinic created successfully" + " with ID "+data.data.clinic.id;
           }).catch(function (response) {
                if(response.data.message !== undefined){
                  $scope.clinicStatus.message = response.data.message;
                }else{
                  $scope.clinicStatus.message = 'Error occured! Please try again';
                }
               $scope.clinicStatus.isMessage = true;
            });
         };

         $scope.deleteClinic = function(){
               $scope.clinic.id = 1;
               ClinicService.deleteClinic($scope.clinic.id).then(function (data) {      	
               $scope.clinicStatus.isMessage = true;
               $scope.clinicStatus.message = data.data.message;
         }).catch(function (response) {
              if(response.data.message !== undefined){
                $scope.clinicStatus.message = data.data.message;
              }else{
               $scope.clinicStatus.message = 'Error occured! Please try again';
              }
              $scope.clinicStatus.isMessage = true;
          });
       };
     }
    };
});






