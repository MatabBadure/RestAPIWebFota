'use strict';

angular.module('hillromvestApp')
  .directive('patientList', function (UserService) {
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
	      

	      var init = function(){
	      	$scope.patients =[];
	      	$scope.patientInfo ={};
	      	$scope.currentPageIndex = 1;
	      	$scope.perPageCount = 10;
	      };

	      init();

	      $scope.selectPatient = function(patient) {
	      	// REST Call to get patient details for patient ID
	      	UserService.getPatientInfo(patient.id).then(function (response) {
            $scope.patientInfo = response.data;  
	          }).catch(function (response) {
	          	console.log("get Patient Info failed!");
	            
	         });
	        $scope.patient = $scope.patientInfo;
	        $scope.onSelect({'patient': patient});
	      },

	      $scope.createPatient = function(){
	      	$scope.onCreate();
	      },

	      $scope.searchPatients = function(track){
	      	if(track!==undefined){
	      		if(track === "prev" && $scope.currentPageIndex >1)
	      			$scope.currentPageIndex--;
	      		if(track === "next")
	      			$scope.currentPageIndex++;
	      	}else
	      	//$scope.searchItem ="ravi";
	      	UserService.getPatientList($scope.searchItem).then(function (response) {
            $scope.patients = response.data;  
          }).catch(function (response) {
          	console.log("get Patient List failed!");
            /*$scope.isMessage = true;  
            if(response.data.message != undefined){
             $scope.patientStatus.message = response.data.message;
           }else{
             $scope.patientStatus.message = 'Error occured! Please try again';
           }*/
         });
	        /*$scope.patients = [{'PID':'43','hillromId':'15','gender':'Male','dob':'1990-05-08','firstName':'Johny','lastName':'Dep','name':'Johny Dep','email':'JohnyDep@gmail.com','hospital':'Appolo hospital'}
	        ,{'PID':'21','hillromId':'16','dob':'1990-05-08','gender':'Male','name':'James williams','email':'JamesWilliams@gmail.com','hospital':'Manipal hospital'}
	        ,{'PID':'21','hillromId':'17','dob':'1990-05-08','gender':'Male','name':'David Jones','email':'davijones@gmail.com','hospital':'abc hospital'}
	        ,{'PID':'21','hillromId':'18','dob':'1990-05-08','gender':'Male','name':'William Davis','email':'williamdavis@gmail.com','hospital':'mno hospital'}
	        ,{'PID':'21','hillromId':'19','dob':'1990-05-08','gender':'Male','name':'Joseph taylor','email':'josephtaylor@gmail.com','hospital':'xyz hospital'}
	        ,{'PID':'21','hillromId':'20','dob':'1990-05-08','gender':'Male','name':'David Jones','email':'davijones@gmail.com','hospital':'abc hospital'}];*/
	      } 
	    }

    };
  });

