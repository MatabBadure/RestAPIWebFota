'use strict';

angular.module('hillromvestApp')
    .controller('PatientsController', function ($scope) {
    	$scope.patient = {};

    	 $scope.patientStatus =
		    {
		        'isCreate':true,
		        'isMessage':false
		    };
    	$scope.selectedPatient = function(patient) {
	        $scope.patientStatus =
			    {
			        'isCreate':false,
			        'isMessage':false
			    };
	        $scope.patient = patient;
    	};

    });