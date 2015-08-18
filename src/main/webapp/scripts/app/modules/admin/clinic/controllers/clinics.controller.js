'use strict';

angular.module('hillromvestApp')
  .controller('clinicsController', function ($rootScope, $scope, $state, $stateParams, $timeout, Auth, clinicService, UserService, notyService) {
    $scope.clinic = {};
    $scope.clinicStatus = {
      'role':localStorage.getItem('role'),
      'editMode':false,
      'isCreate':false,
      'isMessage':false,
      'message': ''
    }
    /*check the state from the route*/
    $scope.init = function() {
      var currentRoute = $state.current.name;     
      if (currentRoute === 'clinicEdit') {
        $scope.initClinicEdit($stateParams.clinicId, $scope.setEditMode);
        //$scope.getClinicDetails($stateParams.clinicId, $scope.setEditMode);
      } else if (currentRoute === 'clinicNew') {
        $scope.initCreateClinic();
      } else if (currentRoute === 'clinicUser'){
        $scope.initClinicList();
      } else if (currentRoute === 'clinicProfile'){
        $scope.initClinicProfile($stateParams.clinicId);
      }
    };
    
    $scope.initClinicList = function(){
      $scope.currentPageIndex = 1;
      $scope.perPageCount = 10;
      $scope.pageCount = 0;
      $scope.total = 0;
      $scope.clinics = [];
      $scope.sortOption ="";
      $scope.showModal = false;
    }    

    $scope.initCreateClinic = function(){
      $scope.states = [];
      $scope.clinic = {};
      $scope.clinic.type = 'parent';
      $scope.clinicStatus.editMode = false;
      $scope.clinicStatus.isCreate = true;
      UserService.getState().then(function(response) {
        $scope.states = response.data.states;
      }).catch(function(response) {});
      if($stateParams.parentId){
        $scope.clinicStatus.createSatellite = true;
        $scope.clinic.type = "child";        
        clinicService.getClinic($stateParams.parentId).then(function(response) {  
        $scope.clinic.parentClinic = response.data.clinic;        
      }).catch(function(response) {});
      }else{
        $scope.getParentClinic();
      }      
    }

    $scope.initClinicEdit = function(clinicId){
      $scope.states = [];
      $scope.clinicStatus.editMode = true;
      $scope.clinicStatus.isCreate = false;
      UserService.getState().then(function(response) {
        $scope.states = response.data.states;
      }).catch(function(response) {});
      clinicService.getClinic(clinicId).then(function(response) {
        $scope.clinic = response.data.clinic;  
        if($scope.clinic.parent){
          $scope.clinic.type = "parent";
        }else{
           $scope.clinic.type = "child";
        }
      }).catch(function(response) {});
    }

    $scope.initClinicProfile = function(clinicId){
      $scope.states = [];
      $scope.clinicStatus.editMode = true;
      $scope.clinicStatus.isCreate = false;     
      clinicService.getClinic(clinicId).then(function(response) {
        $scope.clinic = response.data.clinic; 
        $scope.childClinics = response.data.childClinics; 
        if($scope.clinic.parent){
          $scope.clinic.type = "parent";
        }else{
           $scope.clinic.type = "child";
        }
        if($scope.clinic.deleted){
          $scope.clinic.status = "Inactive";
        }else{
          $scope.clinic.status = "Active";
        }
        /*if(){

        }*/
      }).catch(function(response) {});
    }

    /* init clinic list*/

    var timer = false;
      $scope.$watch('searchItem', function () {
        if($state.current.name === 'clinicUser'){
        if(timer){
          $timeout.cancel(timer)
        }
        timer= $timeout(function () {
            $scope.searchClinics();
        },1000)
       }
      });

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

      $scope.selectClinic = function(clinic) {
         $state.go('clinicProfile', {
          'clinicId': clinic.id
        });
      };

      $scope.createClinic = function(){
          $state.go('clinicNew');
      };
    /* init clinic list*/

    /* edit clinic */
    $scope.open = function () {
      $scope.showModal = true;
    };

    $scope.close = function () {
      $scope.showModal = false;
    };

    $scope.newChildClinic = function() {
      $scope.clinic.childClinics.push({
        name: ''
      });
    };

    $scope.removeChildClinic = function(idx) {
      $scope.clinic.childClinics.splice(idx, 1);
    };

    $scope.submitted = false;
    $scope.formSubmit = function() {
      $scope.submitted = true;
    };
    
    $scope.formSubmitClinic = function() {
      if ($scope.form.$invalid) {
        return false;
      }
      if ($scope.clinic.type === 'parent') {
        $scope.clinic.parent = true;
      } else {
        $scope.clinic.parent = false;
      }
      if ($scope.clinicStatus.editMode) {
        var data = $scope.clinic;
        if (data.parentClinic) {
          var id = data.parentClinic.id;
          var name = data.parentClinic.name;
          delete data.parentClinic;
          data.parentClinic ={};
          data.parentClinic.id = id;
          $scope.clinic.parentClinic.name = name;
        }
        $scope.editClinic(data);
      } else {
        if ($scope.clinic.type === 'parent' && $scope.clinic.parentClinic) {
          delete $scope.clinic.parentClinic;
        }
        $scope.newClinic($scope.clinic);
      }
    };

    $scope.editClinic = function(clinic){
      clinicService.updateClinic(clinic).then(function(data) {
        $scope.clinicStatus.isMessage = true;
        $scope.clinicStatus.message = "Clinic updated successfully";
        notyService.showMessage($scope.clinicStatus.message, 'success');
        $scope.selectClinic(clinic);
        //$scope.clinicStatus.message = "Clinic updated successfully" + " for ID " + data.data.Clinic.id;
        
        /*/*$scope.init();
        $scope.reset();
        $state.go('clinicUser');*/
      }).catch(function(response) {
        if (response.data.message !== undefined) {
          $scope.clinicStatus.message = response.data.message;
        } else if(response.data.ERROR !== undefined){
          $scope.clinicStatus.message = data.data.ERROR;
        } else {
          $scope.clinicStatus.message = 'Error occurred! Please try again';
        }
        $scope.clinicStatus.isMessage = true;
        notyService.showMessage($scope.clinicStatus.message, 'warning');
      });
    };

    $scope.newClinic = function(clinic){
      clinicService.createClinic(clinic).then(function(data) {
        $scope.clinicStatus.isMessage = true;
        $scope.clinicStatus.message = "Clinic created successfully";
        notyService.showMessage($scope.clinicStatus.message, 'success');
        if($scope.clinicStatus.createSatellite){
          $scope.selectClinic(data.data.Clinic.parentClinic);
        }else if($scope.clinicStatus.isCreate){
          $state.go('clinicUser');
        }        
      }).catch(function(response) {
        if (response.message !== undefined) {
          $scope.clinicStatus.message = response.message;
        } else if(response.ERROR !== undefined) {
          $scope.clinicStatus.message = response.ERROR;
        } else {
          $scope.clinicStatus.message = 'Error occured! Please try again';
        }
        $scope.clinicStatus.isMessage = true;
        notyService.showMessage($scope.clinicStatus.message, 'warning');
      });
    };

    $scope.deleteClinic = function() {
      clinicService.deleteClinic($scope.clinic.id).then(function(data) {
        $scope.showModal = false;
        $scope.clinicStatus.isMessage = true;
        $scope.clinicStatus.message = data.data.message;
        notyService.showMessage($scope.clinicStatus.message, 'success');
        $scope.reset();
      }).catch(function(response) {
         $scope.showModal = false;
        if (response.data.message !== undefined) {
          $scope.clinicStatus.message = response.data.message;
        } else if(response.data.ERROR !== undefined){
          $scope.clinicStatus.message = response.data.ERROR;
        } else {
          $scope.clinicStatus.message = 'Error occured! Please try again';
        }
        $scope.clinicStatus.isMessage = true;
        notyService.showMessage($scope.clinicStatus.message, 'warning');
      });
    };

    $scope.cancel = function(){
      $state.go('clinicUser');
    };

    $scope.reset = function(){
      $scope.clinicStatus.editMode = false;
      $scope.clinicStatus.isCreate = false;
      $scope.submitted = false;
      $scope.clinic = {};
      $scope.form.$setPristine();
      $state.go('clinicUser');
    };

    $scope.selectClinicEdit = function(clinic) {
      $scope.clinic.parentClinic.name = clinic.name;
      $scope.clinic.parentClinic.id = clinic.id;
      $scope.clinics = [];
    };

    $scope.removeParent = function() {
      $scope.clinic.parentClinic = null;
    };

    $scope.getParentClinic = function() {
      clinicService.getAllClinics().then(function (response) {
        $scope.clinics = response.data;
      }).catch(function (response) {});
    };
    /* edit clinic */

   /* $scope.getClinicDetails = function(clinicId, callback) {
      clinicService.getClinic(clinicId).then(function(response) {
        $scope.clinic = response.data;
        if (typeof callback === 'function') {
          callback($scope.clinic);
        }
      }).catch(function(response) {});
    };*/

    /*$scope.setEditMode = function(clinic) {
      $scope.clinicStatus.editMode = true;
      $scope.clinicStatus.isCreate = false;
      $scope.clinic = clinic;
      if($scope.clinic.parent === true){
        $scope.clinic.type = 'parent';
      }else{
        $scope.clinic.type = 'child';
      }
    };

    $scope.createClinic = function(){
      $scope.clinicStatus.isCreate = true;
      $scope.clinicStatus.isMessage = false;
    };
*/
    /* clinic profile view*/
    $scope.getHCPs = function(clinicId){      
      var clinicIds = [];
      clinicIds.push(clinicId);
      $state.go('hcpUser', {
        'clinicIds': clinicIds
      });
    }

    $scope.getPatients = function(clinicId){
      var clinicIds = [];
      clinicIds.push(clinicId);
      $state.go('patientUser', {
        'clinicIds': clinicIds
      });
    }

    $scope.createSatellite = function(parentId){            
      $state.go('clinicNew', {
        'parentId': parentId
      });
    }

    $scope.goToEditClinic = function(clinicId){      
      $state.go('clinicEdit', {
        'clinicId': clinicId
      });
    }
    /* clinic profile view*/
    $scope.init();
  });