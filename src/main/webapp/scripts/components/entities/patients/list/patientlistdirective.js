'use strict';

angular.module('hillromvestApp')
  .directive('patientList', function (UserService,PatientService) {
	    return {
	      templateUrl: 'scripts/components/entities/patients/list/patientlist.html',
	      restrict: 'E',
	      scope: {
	      onSelect: '&',
	      onCreate: '&'
	    },
	    link: function(scope, element, attrs) {
	      var patient = scope.patient;
	    },
	    controller: function($scope) {
	      

	      $scope.init = function(){
	      	$scope.patients =[];
	      	$scope.patientInfo ={};
	      	$scope.currentPageIndex = 1;
	      	$scope.perPageCount = 10;
	      	$scope.pageCount = 0;
	      	$scope.total = 0;
	      };

	      $scope.init();

	      $scope.selectPatient = function(patient) {
	      	PatientService.getPatientInfo(patient.id).then(function (response) {
            $scope.patientInfo = response.data;  
            $scope.patient = $scope.patientInfo;
	        $scope.onSelect({'patient': $scope.patient});
	          }).catch(function (response) {
	          	console.log("get Patient Info failed!");
	            
	         });
	        
	      },

	      $scope.createPatient = function(){
	      	$scope.onCreate();
	      },

	      $scope.searchPatients = function(track){
	      	if(track!==undefined){
	      		if(track === "PREV" && $scope.currentPageIndex >1)
	      			$scope.currentPageIndex--;
	      		if(track === "NEXT")
	      			$scope.currentPageIndex++;
	      	}
	      	PatientService.getPatientList($scope.searchItem,$scope.currentPageIndex,$scope.perPageCount)
	      	.then(function (response) {
            	$scope.patients = response.data;
            	$scope.total = response.headers()['x-total-count'];
            	$scope.pageCount = Math.floor($scope.total / 10)+1;
          		}).catch(function (response) {
          		console.log("get Patient List failed!"); 
         });
	      
	      } 
	    }

    };
  });

