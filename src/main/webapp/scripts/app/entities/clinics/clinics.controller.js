'use strict';

angular.module('hillromvestApp')
    .controller('ClinicsController', function ($rootScope, $scope, $state, $timeout, Auth) {
    	$scope.clinic = {};

    $scope.selectedClinic = function(clinic) {
      console.info(clinic, 'controller');
      $scope.clinic = clinic;
    };

    });