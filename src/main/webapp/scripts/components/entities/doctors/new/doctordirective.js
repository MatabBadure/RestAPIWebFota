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
          isCreate:'=isCreate',
          isDoctorCreated:'=isDoctorCreated',
          isDoctorDeleted:'=isDoctorDeleted'
      },
      controller: function($scope){
    	  $scope.doctor = {};
    	  $scope.createDoctor = function(){
    		  var data ={
    				  'title' : 'Dr',
    				  'clinic' : $scope.doctor.clinicName,
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
    		  
    		  Doctor.createDoctor(data).then(function (data) {
             $scope.isDoctorCreated = true;
            }).catch(function () {
              $scope.isDoctorCreated = true;   
            });
    	  }

        $scope.deleteDoctor = function(){
          $scope.doctor.id=1;
          Doctor.deleteDoctor($scope.doctor.id).then(function (data) {
              $scope.isDoctorDeleted = true;
            }).catch(function () {
              $scope.isDoctorDeleted = true;
            });
          };


      }
    };
  });
