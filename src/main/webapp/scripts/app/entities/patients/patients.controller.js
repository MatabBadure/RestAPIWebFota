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
            var _month = dateArr[1];
            _month = _month.length > 1 ? _month : '0' + _month;
            var _day = dateArr[2];
            _day = _day.length > 1 ? _day : '0' + _day;
            var _year = dateArr[0];
            var dob = _month+"/"+_day+"/"+_year;
			$scope.patient.dob = dob;
		}

	};

	$scope.createPatient = function(){
		$scope.patientStatus.isCreate = true;
		$scope.patientStatus.isMessage = false;
	}

});