'use strict';

angular.module('hillromvestApp')
.controller('DoctorsController', function ($rootScope, $scope, $state, $timeout, Auth) {
	$scope.doctor ={};
	$scope.doctorStatus = {
		'isCreate':true,
		'isDoctorCreated':false,
		'isDoctorDeleted':false
	};  	
	$scope.selectedDoctor = function(doctor) {
		$scope.doctorStatus = {
         'isCreate':false,
         'isDoctorCreated':false,
         'isDoctorDeleted':false
     };
     $scope.doctor = doctor;
 };
});