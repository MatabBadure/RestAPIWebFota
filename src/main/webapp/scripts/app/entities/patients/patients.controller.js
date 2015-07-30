'use strict';

angular.module('hillromvestApp')
.controller('PatientsController', function ($scope,localStorageService) {

	$scope.getAge = function(selectedDate){
		var currentDate = new Date();
		var selectedDate = selectedDate;
		  var years = currentDate.getFullYear() - selectedDate.getFullYear();
          var age = 0;
          age = years;
          if(years == 0){
            age = 1;
          }
          if (years < 0) {
            age = 0;
          };
          return age;
	}
	$scope.patient = {};

	$scope.patientStatus =
	{
		'role':localStorage.getItem('role'),
		'editMode':false,
		'isCreate':false,
		'isMessage':false,
		'message': ''
	};
	$scope.selectedPatient = function(patient) {
		$scope.patientStatus.editMode = true;
		$scope.patientStatus.isCreate = false;
		$scope.patient = patient;
		if(patient.dob !== null){
			$scope.patient.age = $scope.getAge(new Date($scope.patient.dob))
			var dateArr = $scope.patient.dob.split('-');
			$scope.patient.formatedDOB = dateArr[1]+"/"+dateArr[2]+"/"+dateArr[0].slice(-2);
		}
		$scope.patient.zipcode = $scope.patient.zipcode;

	};

	$scope.createPatient = function(){
		$scope.patientStatus.isCreate = true;
		$scope.patientStatus.isMessage = false;
	}

});