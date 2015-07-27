'use strict';

angular.module('hillromvestApp')
.controller('PatientsController', function ($scope,localStorageService) {

	$scope.getAge = function(selectedDate){
		var currentDate = new Date();
		var selectedDate = selectedDate;
		var diff = currentDate - selectedDate ;
		var years = Math.floor(diff/(1000*60*60*24*365));
		var age;
		age = years +1;
		if(diff < 0){
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
		'isMessage':false
	};
	$scope.selectedPatient = function(patient) {
		$scope.patientStatus.editMode = true;
		$scope.patientStatus.isCreate = false;
		$scope.patient = patient;
		if(patient.dob != null){
			$scope.patient.age = $scope.getAge(new Date($scope.patient.dob))
			var dateArr = $scope.patient.dob.split('-');
			$scope.patient.formatedDOB = dateArr[1]+"/"+dateArr[2]+"/"+dateArr[0];
		}
		$scope.patient.zipcode = $scope.patient.zipCode;
		
	};

	$scope.createPatient = function(){
		$scope.patientStatus.isCreate = true;
	}

});