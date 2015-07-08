'use strict';

angular.module('hillromvestApp')
    .controller('DoctorsController', function ($rootScope, $scope, $state, $timeout, Auth) {
    	$scope.doctor ={};
    	$scope.selectedDoctor = function(doctor) {
      console.info(doctor, 'controller');
      $scope.doctor = doctor;
    };

    });