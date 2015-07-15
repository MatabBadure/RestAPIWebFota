'use strict';

angular.module('hillromvestApp')
.controller('DoctorsController', function ($rootScope, $scope, $state, $timeout, Auth) {
	$scope.doctor ={};
	$scope.doctorStatus = 
	{
		'isCreate':true,
		'isMessage':false
	};  	
	$scope.selectedDoctor = function(doctor) {
		$scope.doctorStatus = 
	{
         'isCreate':false,
         'isMessage':false
    };
     $scope.doctor = doctor;
 };
});