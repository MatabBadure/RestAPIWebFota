'use strict';

angular.module('hillromvestApp')
.directive('doctor', function (Doctor) {
  return {
    templateUrl: 'scripts/components/entities/doctors/new/create.html',
    restrict: 'E',
    link: function postLink(scope, element, attrs) {
    },
    scope: {
      doctor:'=doctorData',
      doctorStatus:'=doctorStatus'
      
    },
    controller: function($scope){
     $scope.createDoctor = function(){
      var data ={
			  'title' : 'Dr',
			  'clinic' : $scope.doctor.clinic,
			  'firstName' : $scope.doctor.firstName,
			  'middleName' :$scope.doctor.middleName,
			  'lastName' : $scope.doctor.lastName,
			  'speciality' : $scope.doctor.speciality,
			  'credentials' : $scope.doctor.credentials,
			  'email' : $scope.doctor.email,
			  'primaryPhone' : $scope.doctor.primaryPhone,
			  'mobilePhone' : $scope.doctor.mobilePhone,
			  'faxNumber' : $scope.doctor.faxNumber,
			  'address' : $scope.doctor.address,
			  'zipcode' : $scope.doctor.zipCode,
			  'city' : $scope.doctor.city,
			  'state' : $scope.doctor.state,
			  'role': 'DOCTOR'
			  		 
	  }

      Doctor.createDoctor(data).then(function (response) {
       $scope.doctorStatus.isMessage = true;
       $scope.doctorStatus.message = response.data.message+" with ID "+response.data.user.id;
     }).catch(function (response) {
       $scope.doctorStatus.isMessage = true;  
       if(response.data.message != undefined){
        $scope.doctorStatus.message = response.data.message;
      }else{
        $scope.doctorStatus.message = 'Error occured! Please try again';
      }
    });
   }

   $scope.deleteDoctor = function(){
    $scope.doctor.id=1;
    Doctor.deleteDoctor($scope.doctor.id).then(function (response) {
       $scope.doctorStatus.isMessage = true;
       $scope.doctorStatus.message = response.data.message;
    }).catch(function (response) {
       $scope.doctorStatus.isMessage = true;
       if(response.data.message != undefined){
        $scope.doctorStatus.message = response.data.message;
      }else{
        $scope.doctorStatus.message = 'Error occured! Please try again';
      }
    });
  };


}
};
});
