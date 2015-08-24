'use strict';


angular.module('hillromvestApp')
.filter('range', function() {
  return function(val, range) {
    range = parseInt(range);
    for (var i=0; i<range; i++)
      val.push(i);
    return val;
  };
})
.controller('patientsController', function($scope, $filter, $state, $stateParams, patientService, dateService, notyService, UserService, clinicService,$rootScope,$timeout) {
    $scope.patient = {};
    $scope.patientTab = "";
    $scope.newProtocolPoint = 1;
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

    $scope.setOverviewMode = function(patient){
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
      patientService.getHCPsLinkedToPatient($scope.patient.id).then(function(response){
        var hcpUsers = '';
        angular.forEach(response.data.hcpUsers, function(hcpUser){
          if(hcpUsers){
            hcpUsers = hcpUsers + ', ';
          } if(hcpUser.title){
            hcpUsers = hcpUsers + hcpUser.title + ' ' +hcpUser.firstName + ' ' + hcpUser.lastName;
          }else{
            hcpUsers = hcpUsers + hcpUser.firstName + ' ' + hcpUser.lastName;
          }
        });
        $scope.patient.hcpUSers = hcpUsers;
      }).catch(function(response){});
    };

    $scope.initPatientOverview = function(){
      $scope.patientTab = "patientEdit";
      $scope.getPatiendDetails($stateParams.patientId, $scope.setOverviewMode);
    };

    $scope.initpatientDemographic = function(){
      $scope.getPatientById($stateParams.patientId);
      UserService.getState().then(function(response) {
       $scope.states = response.data.states;
      }).catch(function(response) {});
      $scope.getPatiendDetails($stateParams.patientId, $scope.setEditMode);
    };

    $scope.openEditDetail = function(){
      $state.go('patientDemographicEdit', {'patientId': $stateParams.patientId});
    };

    $scope.getProtocols = function(patientId){
      patientService.getProtocol(patientId).then(function(response){
        $scope.protocols = response.data.protocol;
        $scope.addProtocol = true;
        angular.forEach($scope.protocols, function(protocol){
          if(!protocol.deleted){
            $scope.addProtocol = false;
          }
        });
      }).catch(function(){});
    };

    $scope.initProtocolDevice = function(patientId){
      $scope.getPatientById(patientId);
      patientService.getDevices(patientId).then(function(response){
        angular.forEach(response.data.deviceList, function(device){
          var _date = dateService.getDate(device.createdDate);
          var _month = dateService.getMonth(_date.getMonth());
          var _day = dateService.getDay(_date.getDate());
          var _year = dateService.getYear(_date.getFullYear());
          var date = _month + "/" + _day + "/" + _year;
          device.createdDate = date;
          device.days = dateService.getDays(_date);
        });
        $scope.devices = response.data.deviceList;
      }).catch(function(response){});
      $scope.getProtocols(patientId);
    };

    $scope.initPatientAddProtocol = function(){
      $scope.getPatientById($stateParams.patientId);
      $scope.protocol = $stateParams.protocol;
      if(!$scope.protocol){
        $scope.protocol = {};
        $scope.protocol.type = 'Normal';
        $scope.protocol.protocolEntries = [{}];
      } else {
        $scope.protocol.type = $scope.protocol.protocol[0].type;
        $scope.protocol.treatmentsPerDay = $scope.protocol.protocol[0].treatmentsPerDay;
        $scope.protocol.protocolEntries = $scope.protocol.protocol;
      }
    };

    $scope.initPatientAddDevice = function(){
      $scope.getPatientById($stateParams.patientId);
      $scope.device = $stateParams.device;
    };

    $scope.init = function() {
      var currentRoute = $state.current.name;
      //in case the route is changed from other thatn switching tabs
      $scope.patientTab = currentRoute;
      if(currentRoute === 'patientOverview'){
        $scope.initPatientOverview();
      }else if(currentRoute === 'patientDemographic'){
        $scope.initpatientDemographic();
      }else if (currentRoute === 'patientEdit') {
        $scope.getPatiendDetails($stateParams.patientId, $scope.setEditMode);
      } else if (currentRoute === 'patientNew') {
        $scope.createPatient();
      }else if($state.current.name === 'patientEditClinics'){
        $scope.initPatientClinics($stateParams.patientId);
      }else if(currentRoute === 'patientClinics'){
        $scope.initPatientClinicsInfo($stateParams.patientId);
      }else if(currentRoute === 'patientCraegiver'){
        $scope.initpatientCraegiver($stateParams.patientId);
      } else if($state.current.name === 'patientProtocol'){
        $scope.initProtocolDevice($stateParams.patientId);
      }else if(currentRoute === 'patientCraegiverAdd'){
        $scope.initpatientCraegiverAdd($stateParams.patientId);
      }else if(currentRoute === 'patientCraegiverEdit'){
        $scope.initpatientCraegiverEdit($stateParams.patientId);
      }else if(currentRoute === 'patientAddProtocol'){
        $scope.initPatientAddProtocol();
      }else if(currentRoute === 'patientAddDevice'){
        $scope.initPatientAddDevice();
      }else if(currentRoute === 'patientDemographicEdit'){
        $scope.initpatientDemographic();
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
    $scope.availableClinicsForPatient = function(associatedClinics){
      clinicService.getClinics($scope.searchItem, $scope.sortOption, $scope.currentPageIndex, $scope.perPageCount).then(function (response) {
          $scope.clinics = response.data;
          $scope.total = response.headers()['x-total-count'];
          $scope.pageCount = Math.ceil($scope.total / 10);
        }).catch(function (response) {

        });
    }

    /** starts for patient clinics **/
    $scope.getPatientClinicInfo = function(patientId){
      $scope.associatedClinics =[]; $scope.associatedClinics.length = 0;
      patientService.getClinicsLinkedToPatient(patientId).then(function(response) {
        $scope.associatedClinics = response.data.clinics;
      }).catch(function(response) {});
    };

    $scope.disassociateLinkedClinics = function(id, index){
      var data = [{"id": id}];
      patientService.disassociateClinicsFromPatient($stateParams.patientId, data).then(function(response) {
        $scope.associatedClinics = response.data.clinics;
        notyService.showMessage(response.data.message, 'success');
      }).catch(function(response) {
        notyService.showMessage(response.data.message, 'warning');
      });
    };

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
      if($scope.searchItem && $scope.searchItem.length > 0){
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
      }else {
        $scope.clinics = []; $scope.clinics.length = 0;
        $scope.searchItem = "";
      }
    };

    $scope.selectClinicForPatient = function(clinic, index){
      var data = [{"id": clinic.id, "mrnId": null, "notes": null}]
      $scope.searchItem = "";
      patientService.associateClinicToPatient($stateParams.patientId, data).then(function(response) {
        $scope.associatedClinics = response.data.clinics;
      }).catch(function(response) {});
    };

    $scope.initPatientClinicsInfo = function(patientId){
      $scope.patientTab = "patientClinics";
      $scope.currentPageIndex = 1;
      $scope.perPageCount = 90;
      $scope.pageCount = 0;
      $scope.total = 0;
      $scope.clinics = [];
      $scope.sortOption = "";
      $scope.searchItem = "";
      $scope.searchClinicText = false;
      $scope.associatedClinics = [];
      $scope.getPatientById(patientId);
      $scope.getPatientClinicInfo(patientId);
      $scope.getClinics();
    }

    $scope.getClinics = function(){
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
          $state.go('patientDemographic', {'patientId': $stateParams.patientId});
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

    $scope.cancelProtocolDevice = function() {
      $state.go('patientProtocol');
    };

    $scope.cancelEditDemographics = function(){
      $state.go('patientDemographic', {'patientId': $stateParams.patientId});
    };

    $scope.disassociatePatient =function(){
      patientService.disassociatePatient($scope.patient.id).then(function(response){
        notyService.showMessage(response.data.message, 'success');
        $state.go('patientUser');
      }).catch(function(response){});
    };

    /** start of caregiver tab for admin->patient **/
    $scope.getCaregiversForPatient = function(patientId){
      patientService.getCaregiversLinkedToPatient(patientId).then(function(response){
        $scope.caregivers =  response.data.caregivers;
      }).catch(function(response){});
    };

    $scope.associateCaregiverstoPatient = function(patientId, careGiver){
        patientService.associateCaregiversFromPatient(patientId, careGiver).then(function(response){
        $scope.caregivers =  response.data.user;
        $scope.associateCareGiver = [];$scope.associateCareGiver.length = 0;
        $scope.switchPatientTab('patientCraegiver');
      }).catch(function(response){
        notyService.showMessage(response.data.ERROR,'warning' );
      });
    };

    $scope.disassociateCaregiversFromPatient = function(caregiverId, index){
        patientService.disassociateCaregiversFromPatient($stateParams.patientId, caregiverId).then(function(response){
        $scope.caregivers.splice(index, 1);
      }).catch(function(response){});
    };

    $scope.initpatientCraegiver = function (patientId){
      $scope.caregivers = [];
      $scope.getPatientById(patientId);
      $scope.getCaregiversForPatient($stateParams.patientId);
    };

    $scope.initpatientCraegiverAdd = function(){
      $scope.getPatientById($stateParams.patientId);
      $scope.careGiverStatus = "new";
      $scope.associateCareGiver = {};
      UserService.getState().then(function(response) {
        $scope.states = response.data.states;
      }).catch(function(response) {});
      UserService.getRelationships().then(function(response) {
        $scope.relationships = response.data.relationshipLabels;
        $scope.associateCareGiver.relationship = $scope.relationships[0];
      }).catch(function(response) {});
    };

    $scope.linkCaregiver = function(){
      $state.go('patientCraegiverAdd', {'patientId': $stateParams.patientId});
    };

    $scope.formSubmitCaregiver = function(){
      $scope.submitted = true;
      if($scope.form.$invalid){
        return false;
      }
      var data = $scope.associateCareGiver;
      data.role = 'CARE_GIVER';
      if($scope.careGiverStatus === "new"){
        $scope.associateCaregiverstoPatient($stateParams.patientId, data);
      }else if($scope.careGiverStatus === "edit"){
        $scope.updateCaregiver($stateParams.patientId, $stateParams.caregiverId , data);
      }
    };

    $scope.linkDevice = function(){
      $state.go('patientAddDevice');
    };

    $scope.addDevice = function(){
      if($scope.addDeviceForm.$invalid){
        return false;
      }
      patientService.addDevice( $stateParams.patientId, $scope.device).then(function(response){
        $state.go('patientProtocol');
      }).catch(function(response){});
    };

    $scope.linkProtocol = function(){
      $state.go('patientAddProtocol');
    };

    $scope.addProtocol = function(){
      if($scope.addProtocolForm.$invalid){
        return false;
      }
      if($scope.protocol.type === 'Custom'){
        angular.forEach($scope.protocol.protocolEntries, function(protocol, index){
          protocol.treatmentLabel = 'point'+ (index + 1);
        })
      }
      patientService.addProtocol($stateParams.patientId, $scope.protocol).then(function(response){
        $state.go('patientProtocol');
      }).catch(function(response){});
    };

    $scope.deleteDevice = function(device){
      if(!device.active){
        return false;
      }
      patientService.deleteDevice($stateParams.patientId, device).then(function(response){
        device.active = false;
        notyService.showMessage(response.data.message, 'success');
      }).catch(function(response){
        notyService.showMessage(response.data.message, 'warning');
      });
    };

    $scope.deleteProtocol = function(id){
      patientService.deleteProtocol($stateParams.patientId, id).then(function(response){
        $scope.getProtocols($stateParams.patientId);
      }).catch(function(response){});
    };

    $scope.initpatientCraegiverEdit = function(careGiverId){
      $scope.careGiverStatus = "edit";
      $scope.getPatientById($stateParams.patientId);
      $scope.editCaregiver(careGiverId);
    };

    $scope.editCaregiver = function(careGiverId){
        UserService.getState().then(function(response) {
          $scope.states = response.data.states;
        }).catch(function(response) {});
        UserService.getRelationships().then(function(response) {
          $scope.relationships = response.data.relationshipLabels;
        }).catch(function(response) {});
        var caregiverId = $stateParams.caregiverId;
        patientService.getCaregiverById($stateParams.patientId, caregiverId).then(function(response){
          $scope.associateCareGiver = response.data.caregiver.user;
          $scope.associateCareGiver.relationship = response.data.caregiver.relationshipLabel;
        }).catch(function(response){});
    };

    $scope.updateCaregiver = function(patientId, caregiverId , careGiver){
      var tempCaregiver = {};
      tempCaregiver.title = careGiver.title;
      tempCaregiver.firstName = careGiver.firstName;
      tempCaregiver.middleName = careGiver.middleName;
      tempCaregiver.lastName = careGiver.lastName;
      tempCaregiver.email = careGiver.email;
      tempCaregiver.address = careGiver.address;
      tempCaregiver.zipcode = careGiver.zipcode;
      tempCaregiver.city = careGiver.city;
      tempCaregiver.state = careGiver.state;
      tempCaregiver.relationship = careGiver.relationship;
      tempCaregiver.primaryPhone = careGiver.primaryPhone;
      tempCaregiver.mobilePhone = careGiver.mobilePhone;
      tempCaregiver.role = careGiver.role;

      patientService.updateCaregiver(patientId,caregiverId, tempCaregiver).then(function(response){
        $scope.associateCareGiver = [];$scope.associateCareGiver.length = 0;
        $scope.switchPatientTab('patientCraegiver');
      }).catch(function(response){});
    };

    $scope.goToCaregiverEdit = function(careGiverId){
      $state.go('patientCraegiverEdit', {'caregiverId': careGiverId});
    };

    $scope.openEditProtocol = function(protocol){
      if(!protocol){
        return false;
      }
      $scope.protocol = protocol;
      patientService.getProtocolById($stateParams.patientId, protocol.id).then(function(response){
        $scope.protocol = response.data;
        $scope.protocol.edit = true;
        $state.go('patientAddProtocol',{protocol: $scope.protocol});
      }).catch(function(response){});
    };

    $scope.openEditDevice = function(device){
      if(!device.active){
        return false;
      }
      device.edit = true;
      $state.go('patientAddDevice',{device: device});
    };

    $scope.updateProtocol = function(){
      if($scope.protocol.id){
        delete $scope.protocol.id;
      }
      if($scope.protocol.patient){
        delete $scope.protocol.patient;
      }
      if($scope.protocol.edit){
        delete $scope.protocol.edit;
      }
      var data = $scope.protocol.protocol;
      patientService.editProtocol($stateParams.patientId, data).then(function(response){
        $state.go('patientProtocol');
      }).catch(function(response){});
    };

    $scope.getPatientById = function(patientid){
      patientService.getPatientInfo(patientid).then(function(response){
        $scope.slectedPatient = response.data;
      }).catch(function(response){});
    };

    $scope.updateDevice = function(){
      if($scope.device.edit){
        delete $scope.device.edit;
      }
      patientService.addDevice($stateParams.patientId, $scope.device).then(function(response){
        $state.go('patientProtocol');
      }).catch(function(response){});
    };

    $scope.addNewProtocolPoint = function (){
      $scope.newProtocolPoint += 1;
      $scope.protocol.protocolEntries.push({});
    };

    $scope.switchtoNormal = function(){
      $scope.protocol.protocolEntries.splice(1);
    };

    $scope.linkClinic = function(){
      $scope.searchClinicText = true;
    };

    $scope.init();
  });
