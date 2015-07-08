'use strict';

angular.module('hillromvestApp')
  .directive('doctor', function () {
    return {
      templateUrl: 'scripts/components/entities/doctors/new/create.html',
      restrict: 'E',
      link: function postLink(scope, element, attrs) {
      },
      scope: {},
      controller: function($scope){
    	  $scope.doctor = {};
    	  $scope.createDoctor = function(){
    		  var data ={
    				  'clinic' : $scope.doctor.clinicName,
    				  'firstName' : $scope.doctor.firstName,
    				  'firstName' : $scope.doctor.firstName,
    				  'lastName' : $scope.doctor.lastName,
    				  'speciality' : $scope.doctor.speciality,
    				  'credentials' : $scope.doctor.credentials,
    				  'email' : $scope.doctor.email,
    				  'primaryPhone' : $scope.doctor.primaryPhone,
    				  'mobilePhone' : $scope.doctor.mobilePhone,
    				  'faxNumber' : $scope.doctor.faxNumber,
    				  'address' : $scope.doctor.address,
    				  'zipCode' : $scope.doctor.zipCode,
    				  'city' : $scope.doctor.city,
    				  'state' : $scope.doctor.state
    				  
    		  }
    		  console.log("doctor form info", data);
    	  }
      }
    };
  });
