'use strict';

angular.module('hillromvestApp')
    .controller('ClinicsController', function ($rootScope, $scope, $state, $timeout, Auth) {
    $scope.clinic = {};
    $scope.isCreate = true;
    $scope.isClinicCreated = false;
    $scope.isClinicDeleted = false;
    $scope.selectedClinic = function(clinic) {
    	$scope.isCreate = false;
    	$scope.isClinicCreated = false;
    	$scope.isClinicDeleted = false;
      $scope.clinic = clinic;
    };

    });


