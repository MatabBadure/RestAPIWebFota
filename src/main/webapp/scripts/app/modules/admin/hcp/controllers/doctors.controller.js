'use strict';

angular.module('hillromvestApp')
  .controller('DoctorsController', function($rootScope, $scope, $state, $timeout, Auth,$stateParams, UserService) {
    $scope.doctor = {};
    $scope.doctorStatus = {
      'role': localStorage.getItem('role'),
      'editMode': false,
      'isCreate': false,
      'isMessage': false,
      'message': ''
    };

    $scope.init = function(){
      var currentRoute = $state.current.name;
      if ($state.current.name === 'editHCP') {
        $scope.getDoctorDetails($stateParams.doctorId, $scope.setEditMode);
      } else if ($state.current.name === 'createHCP') {
        $scope.createDoctor();
      }
    }
    $scope.selectedDoctor = function(doctor) {
      $scope.doctorStatus.editMode = true;
      $scope.doctorStatus.isCreate = false;
      $scope.doctor = doctor;
    };

    $scope.getDoctorDetails = function(doctorId,callback){
      var url = '/api/user/' + doctorId + '/hcp';
      UserService.getUser(doctorId, url).then(function(response) {
        $scope.doctor = response.data.user;
        if (typeof callback === 'function') {
          callback($scope.doctor);
        }
      }).catch(function(response) {});
    };


    $scope.setEditMode = function(doctor){
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
    $scope.init();
  });
