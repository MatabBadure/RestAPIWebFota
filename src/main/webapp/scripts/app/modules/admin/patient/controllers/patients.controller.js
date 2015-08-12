'use strict';

angular.module('hillromvestApp')
  .controller('patientsController', function($scope, $filter, $state, $stateParams, patientService) {
    $scope.getAge = function(selectedDate) {
      var currentDate = new Date();
      var selectedDate = selectedDate;
      var years = currentDate.getFullYear() - selectedDate.getFullYear();
      var age = 0;
      age = years;
      if (years == 0) {
        age = 1;
      }
      if (years < 0) {
        age = 0;
      };
      return age;
    }

    $scope.patient = {};

    $scope.patientStatus = {
      'role': localStorage.getItem('role'),
      'editMode': false,
      'isCreate': false,
      'isMessage': false,
      'message': ''
    };

    $scope.isActive = function(tab) {
      if ($scope.patientTab.indexOf(tab) !== -1) {
        return true;
      } else {
        return false;
      }
    };

    $scope.switchPatientTab = function(status){
      $scope.patientTab = status;
      $state.go(status, {'patientId': $stateParams.patientId});
    };

    $scope.initPatientOverview = function(){
      $scope.patientTab = "patientEdit";
    };

    $scope.init = function() {
      var currentRoute = $state.current.name;
      if($state.current.name === 'patientUser'){
        $scope.initPatientOverview();
      }
      if ($state.current.name === 'patientEdit') {
        $scope.getPatiendDetails($stateParams.patientId, $scope.setEditMode);
      } else if ($state.current.name === 'patientNew') {
        $scope.createPatient();
      }
    };

    $scope.setEditMode = function(patient) {
      $scope.patientStatus.editMode = true;
      $scope.patientStatus.isCreate = false;
      $scope.patient = patient;
      if (patient.dob !== null) {
        $scope.patient.age = $scope.getAge(new Date($scope.patient.dob))
        var _date = new Date($scope.patient.dob);
        var _month = (_date.getMonth() + 1).toString();
        _month = _month.length > 1 ? _month : '0' + _month;
        var _day = (_date.getDate()).toString();
        _day = _day.length > 1 ? _day : '0' + _day;
        var _year = (_date.getFullYear()).toString();
        var dob = _month + "/" + _day + "/" + _year;
        $scope.patient.dob = dob;
        $scope.patient.formatedDOB = _month + "/" + _day + "/" + _year.slice(-2);
      }
    };

    $scope.getPatiendDetails = function(patientId, callback) {
      patientService.getPatientInfo(patientId).then(function(response) {
        $scope.patientInfo = response.data;
        $scope.patient = $scope.patientInfo;
        if (typeof callback === 'function') {
          callback($scope.patient);
        }
      }).catch(function(response) {});
    };

    $scope.createPatient = function() {
      $scope.patientStatus.isCreate = true;
      $scope.patientStatus.isMessage = false;
      $scope.patient = {
        title: 'Mr.'
      };
    };

    $scope.init();
  });
