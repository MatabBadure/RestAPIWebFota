'use strict';

angular.module('hillromvestApp')
    .controller('PatientsController', function ($scope,localStorageService) {

    	$scope.getAge = function(selectedDate){
		                var currentDate = new Date();
		                var selectedDate = selectedDate;
		                var diff = currentDate - selectedDate ;
		                var days = Math.floor(diff/(1000*60*60*24));
		                var years = Math.floor(days/365);
		                var months = ((days % 365)/30).toFixed(1)
		                var age = 0;
		             
		              if(years === 0){
		                if(months > 1){
		                  age = months + " months ";
		                }else{
		                  age = months + " month ";
		                }
		              }
		              if(years > 0){
		                if(years > 1){
		                  if(months > 1){
		                    age = years + " years " + months + " months ";
		                  }else{
		                    age = years + " years " + months + " month ";
		                  }
		                }else{
		                  if(months > 1){
		                    age = years + " year " + months + " months ";
		                  }else{
		                    age = years + " year " + months + " month ";
		                  }
		                }
		              }

		              if(diff < 0){
		                age = age-1;
		              }

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
	        $scope.patientStatus =
			    {
			    	'role':localStorage.getItem('role'),
			    	'editMode':true,
			        'isCreate':false,
			        'isMessage':false
			    };
	        $scope.patient = patient;
	        $scope.patient.age = $scope.getAge(new Date($scope.patient.dob))
    	};

    	$scope.createPatient = function(){
    			$scope.patientStatus.isCreate = true;
    	}

    });