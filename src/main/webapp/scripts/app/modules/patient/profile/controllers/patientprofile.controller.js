'use strict';

angular.module('hillromvestApp').controller('patientprofileController', function ($scope, $state, $stateParams, notyService, patientService) {
	$scope.init = function(){
		var currentRoute = $state.current.name;	
		$scope.profileTab = currentRoute;	
		$scope.userRole = localStorage.getItem('role');
		patientService.getPatientInfo(localStorage.getItem('patientID')).then(function(response){
			$scope.patientView = response.data;			
			if (currentRoute === 'patientProfile') {
				$scope.initProfileView();        
			}else if(currentRoute === 'patientProfileEdit'){
				$scope.initProfileEdit();
			}else if(currentRoute === 'patientResetPassword'){
				$scope.initResetPassword();
			}else if(currentRoute === 'patientSettings'){// html is not available for this template
				$scope.initPatientSettings();
			}
		}).catch(function(response){});
		
	};

	$scope.isProfileTabActive = function(tab) {
      if ($scope.profileTab.indexOf(tab) !== -1) {
        return true;
      } else {
        return false;
      }
    };

	$scope.switchProfileTabs = function(tab){
		$scope.profileTab = tab;
		$state.go(tab);	
	};

	$scope.getPatientById = function(patientId){
      patientService.getPatientInfo(patientId).then(function(response){
        $scope.patientView = response.data;
      }).catch(function(response){});
    };

	$scope.initProfileView = function(){
		/*$scope.userRole = localStorage.getItem('role');
		$scope.getPatientById(localStorage.getItem('patientID'));*/
	};

	$scope.openEditDetail = function(){
		$state.go("patientProfileEdit");	
	};

	$scope.initProfileEdit = function(){
		$scope.editPatientProfile = $scope.patientView;
	};

	$scope.cancelEditProfile = function(){
		$state.go("patientProfile");
		$scope.editPatientProfile = "";
	};

	$scope.updateProfile = function(){

	};

	$scope.init();
    
  });