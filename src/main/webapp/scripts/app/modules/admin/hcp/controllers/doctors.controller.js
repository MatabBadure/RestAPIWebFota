'use strict';

angular.module('hillromvestApp')
  .controller('DoctorsController', function($rootScope, $scope, $state, $timeout, Auth,$stateParams, UserService, DoctorService ) {
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
      if ($state.current.name === 'hcpProfile') {
        $scope.getDoctorDetails($stateParams.doctorId, $scope.setEditMode);
      } else if ($state.current.name === 'hcpNew') {
        $scope.createDoctor();
      }
    };

    $scope.selectedDoctor = function(doctor) {
      $scope.doctorStatus.editMode = true;
      $scope.doctorStatus.isCreate = false;
      $scope.doctor = doctor;
    };

    $scope.getDoctorDetails = function(doctorId,callback){
      $scope.getPatientsAssociatedToHCP(doctorId, null);
      $scope.getClinicsOfHCP(doctorId);
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

    $scope.selectClinic = function(clinic){
      console.log(clinic.selected);
      if(clinic.selected){
      } else {
      }
    };

    $scope.viewAssociatedPatients = function(){
      if($scope.doctor.clinics.length === 1){
        var ids = $scope.doctor.clinics[0].id;
        localStorage.setItem('ids', ids)
        $state.go('associatedPatients');
      } else {
        console.log('Returning False...!')
        return false;
      }

    };

    $scope.onSuccess = function() {
      $scope.$broadcast('resetList', {});
    };

    $scope.getPatientsAssociatedToHCP = function(doctorId, clinicId){
      DoctorService.getPatientsAssociatedToHCP(doctorId, clinicId).then(function(response) {
        $scope.patients = response.data.patientList;
      }).catch(function(response) {});
    };

    $scope.getPatientsByClinic = function(){
      $scope.getPatientsAssociatedToHCP($scope.doctor.id, $scope.sortOption);
    };

    $scope.getClinicsOfHCP = function(doctorId){
      DoctorService.getClinicsAssociatedToHCP(doctorId).then(function(response) {
        $scope.clinicsOfHCP =  response.data.clinics;
        $scope.clinicList = [{"clinicId": 0, "name": "ALL"}];
        $scope.sortOption = $scope.clinicList[0].clinicId;
        for(var i=0; i< $scope.clinicsOfHCP.length; i++){
          $scope.clinicList.push({"clinicId": $scope.clinicsOfHCP[i].id, "name": $scope.clinicsOfHCP[i].name});
        }
      }).catch(function(response) {});
    };

    $scope.init();
  });
