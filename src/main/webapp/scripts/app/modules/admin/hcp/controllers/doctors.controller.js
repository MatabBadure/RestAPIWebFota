'use strict';

angular.module('hillromvestApp')
  .controller('DoctorsController', function($rootScope, $scope, $state, $timeout, Auth) {
    $scope.doctor = {};
    $scope.doctorStatus = {
      'role': localStorage.getItem('role'),
      'editMode': false,
      'isCreate': false,
      'isMessage': false,
      'message': ''
    };
    $scope.selectedDoctor = function(doctor) {
      $scope.doctorStatus.editMode = true;
      $scope.doctorStatus.isCreate = false;
      $scope.doctor = doctor;
    };
    $scope.createDoctor = function() {
      $scope.doctorStatus.isCreate = true;
      $scope.doctorStatus.isMessage = false;
      $scope.doctor = {
        title: 'Mr.'
      };
    };

    $scope.onSuccess = function() {
      $scope.$broadcast('resetList', {});
    };
  });
