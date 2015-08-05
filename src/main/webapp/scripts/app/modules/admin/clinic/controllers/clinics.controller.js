'use strict';

angular.module('hillromvestApp')
  .controller('clinicsController', function ($rootScope, $scope, $state, $stateParams, $timeout, Auth, clinicService) {
    $scope.clinic = {};
    $scope.clinicStatus = {
      'role':localStorage.getItem('role'),
      'editMode':false,
      'isCreate':false,
      'isMessage':false,
      'message': ''
    }
    $scope.selectedClinic = function(clinic) {
      // $scope.clinicStatus.editMode = true;
      // $scope.clinicStatus.isCreate = false;
      // $scope.clinic = clinic;
    };

    $scope.init = function() {
      console.log('Loading Controller...!');
      var currentRoute = $state.current.name;
      if ($state.current.name === 'clinicEdit') {
        $scope.getClinicDetails($stateParams.clinicId, $scope.setEditMode);
      } else if ($state.current.name === 'clinicNew') {
        $scope.createClinic();
      }
    };

    $scope.getClinicDetails = function(clinicId, callback) {
      clinicService.getClinic(clinicId).then(function(response) {
        $scope.clinic = response.data;
        if (typeof callback === 'function') {
          callback($scope.clinic);
        }
      }).catch(function(response) {});
    };

    $scope.setEditMode = function(clinic) {
      $scope.clinicStatus.editMode = true;
      $scope.clinicStatus.isCreate = false;
      $scope.clinic = clinic;
    };

    $scope.createClinic = function(){
      $scope.clinicStatus.isCreate = true;
      $scope.clinicStatus.isMessage = false;
    };

    $scope.init();
  });