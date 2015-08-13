'use strict';

angular.module('hillromvestApp').controller('patientsController', function($scope, $filter, $state, $stateParams, patientService, dateService, notyService, UserService, clinicService) {


    $scope.patient = {};
    $scope.patientTab = "";

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
      console.log(status);
      $state.go(status, {'patientId': $stateParams.patientId});
    };

    $scope.setOverviewMode = function(patient){
      console.log(patient);
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

    $scope.initPatientOverview = function(){
      $scope.patientTab = "patientEdit";
      console.log('Coming Here : ', $stateParams.patientId);
      $scope.getPatiendDetails($stateParams.patientId, $scope.setOverviewMode);
    };

    $scope.initpatientDemographic = function(){
      $scope.getPatiendDetails($stateParams.patientId, $scope.setEditMode);
    };
    $scope.init = function() {
      $scope.patientTab = "patientOverview";
      var currentRoute = $state.current.name;
      if($state.current.name === 'patientOverview'){
        $scope.initPatientOverview();
      }else if($state.current.name === 'patientDemographic'){
        $scope.initpatientDemographic();
      }else if ($state.current.name === 'patientEdit') {
        $scope.getPatiendDetails($stateParams.patientId, $scope.setEditMode);
      } else if ($state.current.name === 'patientNew') {
        $scope.createPatient();
      }else if($state.current.name === 'patientClinics'){
        $scope.initPatientClinicsInfo($stateParams.patientId);
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


    /** starts for patient clinics **/
    $scope.getPatientClinicInfo = function(patientId){
      //$scope.associatedClinics = associatedClinics.clinics;
      //$scope.availableClinicsForPatient($scope.associatedClinics);
      $scope.associatedClinics =[]; $scope.associatedClinics.length = 0;
      patientService.getClinicsLinkedToPatient(patientId).then(function(response) {
        $scope.associatedClinics = response.data.clinics;
      }).catch(function(response) {});
    }

    $scope.disassociateLinkedClinics = function(id, index){
      var data = [{"id": id}];
      patientService.disassociateClinicsFromPatient($stateParams.patientId, data).then(function(response) {
        $scope.associatedClinics = response.data.clinics;
      }).catch(function(response) {});
    }

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
        $scope.clinics = []; $scope.clinics.length = 0;
        $scope.clinics = response.data;
        for(var i=0; i < $scope.associatedClinics.length; i++){
          for(var j=0; j <  $scope.clinics.length; j++ ){
            if($scope.associatedClinics[i].id == $scope.clinics[j].id){
              $scope.clinics.splice(j, 1);
            }
          }
        }
        $scope.total = response.headers()['x-total-count'];
        $scope.pageCount = Math.ceil($scope.total / 10);
      }).catch(function (response) {

      });
    };

    $scope.selectClinicForPatient = function(clinic, index){
      patientService.associateClinicToPatient($stateParams.patientId, clinic).then(function(response) {
        $scope.associatedClinics = response.data.clinics;
        $scope.associatedClinics.splice(index,1);
      }).catch(function(response) {});
    }
    $scope.initPatientClinicsInfo = function(patientId){
      $scope.patientTab = "patientClinics";
      $scope.currentPageIndex = 1;
      $scope.perPageCount = 10;
      $scope.pageCount = 0;
      $scope.total = 0;
      $scope.clinics = [];
      $scope.sortOption ="";
      $scope.associatedClinics = [];
      $scope.getPatientClinicInfo(patientId);

    }

    $scope.formSubmit = function(){
      $scope.submitted = true;
      if($scope.form.$invalid){
        return false;
      }
      var data = $scope.patient;
      data.role = 'PATIENT';
      UserService.editUser(data).then(function (response) {
        if(response.status === 200) {
          $scope.patientStatus.isMessage = true;
          $scope.patientStatus.message = "Patient updated successfully";
          notyService.showMessage($scope.patientStatus.message, 'success');
        } else {
          $scope.patientStatus.message = 'Error occured! Please try again';
          notyService.showMessage($scope.patientStatus.message, 'warning');
        }
      }).catch(function (response) {
        $scope.patientStatus.isMessage = true;
        if (response.data.message !== undefined) {
          $scope.patientStatus.message = response.data.message;
        } else if(response.data.ERROR !== undefined) {
          $scope.patientStatus.message = response.data.ERROR;
        } else {
          $scope.patientStatus.message = 'Error occured! Please try again';
        }
        notyService.showMessage($scope.patientStatus.message, 'warning');
      });
    };

    $scope.cancel = function() {
      console.log('Cancel works...!');
      console.log('$scope.patient :: ', $scope.patient);
    };

    $scope.disassociatePatient =function(){
      patientService.disassociatePatient($scope.patient.id).then(function(response){
        notyService.showMessage(response.data.message, 'success');
        $state.go('patientUser');
      }).catch(function(response){});
    };
    $scope.init();
  });