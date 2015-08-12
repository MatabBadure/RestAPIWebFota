'use strict';

angular.module('hillromvestApp')
  .controller('patientsController', function($scope, $filter, $state, $stateParams, patientService, dateService,clinicService, $timeout) {

    $scope.patient = {};

    $scope.patientStatus = {
      'role': localStorage.getItem('role'),
      'editMode': false,
      'isCreate': false,
      'isMessage': false,
      'message': ''
    };

    $scope.init = function() {
      var currentRoute = $state.current.name;
      if ($state.current.name === 'patientEdit') {
        $scope.getPatiendDetails($stateParams.patientId, $scope.setEditMode);
      } else if ($state.current.name === 'patientNew') {
        $scope.createPatient();
      }else if($state.current.name === 'patientEditClinics'){
        $scope.initPatientClinics($stateParams.patientId);        
      }
    };

    $scope.setEditMode = function(patient) {
      $scope.patientStatus.editMode = true;
      $scope.patientStatus.isCreate = false;
      $scope.patient = patient;
      if (patient.dob !== null) {
        $scope.patient.age = dateService.getAge(new Date($scope.patient.dob));
        var _date = dateService.getDate($scope.patient.dob);
        var _month = dateService.getMonth(_date.getMonth());
        var _day = dateService.getDay(_date.getDate());
        var _year = dateService.getYear(_date.getFullYear());
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
    
    $scope.goToPatientClinics = function(){
      alert("goToPatientClinics"+$stateParams.patientId);
      $state.go('patientEditClinics',{'patientId': $stateParams.patientId});

    }
    /** starts for patient clinics **/
    $scope.getPatientClinicInfo = function(patientId){
      $scope.associatedClinics = associatedClinics.clinics;      
      /*$scope.availableClinicsForPatient($scope.associatedClinics);
      patientService.getClinicsLinkedToPatient(patientId).then(function(response) {
        $scope.associatedClinics = response.data;            
      }).catch(function(response) {});*/
    }
    $scope.disassociateLinkedClinics = function(id, index){
      $scope.associatedClinics.splice(index, 1);
      // API returning error : unAuthorized
     /* patientService.disassociateClinicsFromPatient(id).then(function(response) {
        $scope.associatedClinics = response.data;        
      }).catch(function(response) {});*/
    }
    $scope.availableClinicsForPatient = function(associatedClinics){          
      clinicService.getClinics($scope.searchItem, $scope.sortOption, $scope.currentPageIndex, $scope.perPageCount).then(function (response) {
          $scope.clinics = response.data;
          $scope.total = response.headers()['x-total-count'];
          $scope.pageCount = Math.ceil($scope.total / 10);
        }).catch(function (response) {

        });
    }
    /*var timer = false;
    $scope.$watch('searchItem', function () {
      if(timer){
        $timeout.cancel(timer)
      }
      timer= $timeout(function () {
          $scope.searchClinics();
      },1000)
    });*/
    $scope.searchClinics = function (track) {
      if (track !== undefined) {
        if (track === "PREV" && $scope.currentPageIndex > 1) {
          $scope.currentPageIndex--;
        }
        else if (track === "NEXT" && $scope.currentPageIndex < $scope.pageCount){
            $scope.currentPageIndex++;
        }
        else{
            return false;
        }
      }else {
          $scope.currentPageIndex = 1;
      }
      clinicService.getClinics($scope.searchItem, $scope.sortOption, $scope.currentPageIndex, $scope.perPageCount).then(function (response) {
        $scope.clinics = response.data;
        $scope.total = response.headers()['x-total-count'];
        $scope.pageCount = Math.ceil($scope.total / 10);
      }).catch(function (response) {

      });
    };
    $scope.initPatientClinics = function(patientId){
      $scope.currentPageIndex = 1;
      $scope.perPageCount = 10;
      $scope.pageCount = 0;
      $scope.total = 0;
      $scope.clinics = [];
      $scope.sortOption ="";
      $scope.getPatientClinicInfo(patientId);
    }

    $scope.init();    
  });
