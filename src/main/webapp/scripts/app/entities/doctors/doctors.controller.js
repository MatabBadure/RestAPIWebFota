'use strict';

angular.module('hillromvestApp')
    .controller('DoctorsController', function ($rootScope, $scope, $state, $timeout, Auth) {
    	$scope.doctor ={};
    	$scope.isCreate = true;
    	$scope.isDoctorCreated = false;
    	$scope.isDoctorDeleted = false;
    	$scope.selectedDoctor = function(doctor) {
    		$scope.isCreate = false;
    		$scope.isDoctorCreated = false;
    		$scope.isDoctorDeleted = false;
      		$scope.doctor = doctor;
    };

    });