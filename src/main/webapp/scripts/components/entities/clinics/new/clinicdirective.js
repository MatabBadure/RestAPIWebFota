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
    link: function postLink(scope, element, attrs) {},

    controller: function($scope) {
     $scope.createClinic = function () {
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
       $scope.isClinicCreated = true;
     }).catch(function () {
      $scope.isClinicCreated = false;      		
    });
   };
   
   $scope.deleteClinic = function(){
    $scope.clinic.id = 1;
    ClinicService.deleteClinic($scope.clinic.id).then(function (data) {      	
      $scope.isClinicDeleted = true;
    }).catch(function () {
      $scope.isClinicDeleted = false;
    });
  };
}
};
});






