'use strict';

angular.module('hillromvestApp').controller('patientsController', function($scope, $filter, $state, $stateParams, patientService, dateService, notyService, UserService, clinicService,$rootScope) {


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
      UserService.getState().then(function(response) {
       $scope.states = response.data.states;
      }).catch(function(response) {});
      $scope.getPatiendDetails($stateParams.patientId, $scope.setEditMode);
    };

    $scope.initProtocolDevice = function(patientId){
      console.log('initProtocolDevice Function', patientId);
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
      patientService.getProtocol(patientId).then(function(response){
        $scope.protocol = response.data.protocol;
      }).catch(function(){});
    };

    $scope.initPatientAddProtocol = function(){
      $scope.protocol = $stateParams.protocol;
    };

    $scope.initPatientAddDevice = function(){
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
        $scope.clinics = []; $scope.clinics.length = 0;
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
      patientService.associateClinicToPatient($stateParams.patientId, data).then(function(response) {
        $scope.associatedClinics = response.data.clinics;
        $scope.clinics = []; $scope.clinics.length = 0;
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
      $scope.getPatientById(patientId);
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

    $scope.cancelProtocolDevice = function() {
      $state.go('patientProtocol');
    };

    $scope.disassociatePatient =function(){
      patientService.disassociatePatient($scope.patient.id).then(function(response){
        notyService.showMessage(response.data.message, 'success');
        $state.go('patientUser');
      }).catch(function(response){});
    };

    /*$rootScope.selectPatientFromList = function(patient) {
      $state.go('patientOverview', {
        'patientId': patient.id
      });
    };*/
    /** start of caregiver tab for admin->patient **/
    $scope.getCaregiversForPatient = function(patientId){
      patientService.getCaregiversLinkedToPatient(patientId).then(function(response){
        $scope.caregivers =  response.data.caregivers;
      }).catch(function(response){});
    }
    $scope.associateCaregiverstoPatient = function(patientId, careGiver){
        patientService.associateCaregiversFromPatient(patientId, careGiver).then(function(response){
        $scope.caregivers =  response.data.user;
        $scope.associateCareGiver = [];$scope.associateCareGiver.length = 0;
        $scope.switchPatientTab('patientCraegiver');
      }).catch(function(response){       
        notyService.showMessage(response.data.ERROR,'warning' );
      });
    }
    $scope.disassociateCaregiversFromPatient = function(caregiverId, index){
        patientService.disassociateCaregiversFromPatient($stateParams.patientId, caregiverId).then(function(response){
        $scope.caregivers.splice(index, 1);
      }).catch(function(response){});
    }
    $scope.initpatientCraegiver = function (patientId){
      $scope.caregivers = [];
      $scope.getPatientById(patientId);
      $scope.getCaregiversForPatient($stateParams.patientId);
    }
    $scope.initpatientCraegiverAdd = function(){
      $scope.getPatientById($stateParams.patientId);
      $scope.careGiverStatus = "new";
      UserService.getState().then(function(response) {
        $scope.states = response.data.states;
      }).catch(function(response) {});
      UserService.getRelationships().then(function(response) {
        $scope.relationships = response.data.relationships;
      }).catch(function(response) {});
    }
    $scope.linkCaregiver = function(){
      //$scope.associateCareGiver = "";
      $state.go('patientCraegiverAdd', {'patientId': $stateParams.patientId});
    }

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
    }

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
      }).catch(function(response){});
    };

    $scope.deleteProtocol = function(){
      patientService.deleteProtocol($stateParams.patientId).then(function(response){
        $scope.protocol = "";
      }).catch(function(response){});
    };

    $scope.initpatientCraegiverEdit = function(careGiverId){
      $scope.careGiverStatus = "edit";
      $scope.getPatientById($stateParams.patientId);
      $scope.editCaregiver(careGiverId);
    }

    $scope.editCaregiver = function(careGiverId){
        UserService.getState().then(function(response) {
          $scope.states = response.data.states;
        }).catch(function(response) {});
        UserService.getRelationships().then(function(response) {
          $scope.relationships = response.data.relationships;
        }).catch(function(response) {});
        var caregiverId = $stateParams.caregiverId;
        //$scope.associateCareGiver = caregiver.caregiver.user;        
        patientService.getCaregiverById($stateParams.patientId, caregiverId).then(function(response){
          $scope.associateCareGiver = response.data.caregiver.user;
          //$scope.associateCareGiver.relationship = $scope.associateCareGiver.relationshipLabel;  
          //$scope.associateCareGiver.state = {"name": ""+$scope.associateCareGiver.state};  alert(JSON.stringify($scope.associateCareGiver.state));      
        }).catch(function(response){});
    }
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
    }
    $scope.goToCaregiverEdit = function(careGiverId){
      $state.go('patientCraegiverEdit', {'caregiverId': careGiverId});
    }

    $scope.openEditProtocol = function(){
      if(!$scope.protocol){
        return false;
      }
      $scope.protocol.edit = true;
      $state.go('patientAddProtocol',{protocol: $scope.protocol});
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
      patientService.editProtocol($stateParams.patientId, $scope.protocol).then(function(response){
        $state.go('patientProtocol');
      }).catch(function(response){});
    };

    $scope.getPatientById = function(patientid){
      patientService.getPatientInfo(patientid).then(function(response){
        $scope.slectedPatient = response.data;
      }).catch(function(response){});
    }

    $scope.updateDevice = function(){
      if($scope.device.edit){
        delete $scope.device.edit;
      }
      patientService.addDevice($stateParams.patientId, $scope.device).then(function(response){
        $state.go('patientProtocol');
      }).catch(function(response){});
    };
    $scope.init();
  });
