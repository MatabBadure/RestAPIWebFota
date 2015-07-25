'use strict';

angular.module('hillromvestApp')
  .directive('patientList', function () {
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
	      $scope.patients =[];

	      $scope.selectPatient = function(patient) {
	        $scope.patient = patient;
	        $scope.onSelect({'patient': patient});
	      },
	      $scope.createPatient = function(){
	      	$scope.onCreate();
	      },

	      $scope.searchPatients = function(){
	        $scope.patients = [{'PID':'43','hillromId':'15','gender':'Male','dob':'1990-05-08','firstName':'Johny','lastName':'Dep','name':'Johny Dep','email':'JohnyDep@gmail.com','hospital':'Appolo hospital'}
	        ,{'PID':'21','hillromId':'16','dob':'1990-05-08','gender':'Male','name':'James williams','email':'JamesWilliams@gmail.com','hospital':'Manipal hospital'}
	        ,{'PID':'21','hillromId':'17','dob':'1990-05-08','gender':'Male','name':'David Jones','email':'davijones@gmail.com','hospital':'abc hospital'}
	        ,{'PID':'21','hillromId':'18','dob':'1990-05-08','gender':'Male','name':'William Davis','email':'williamdavis@gmail.com','hospital':'mno hospital'}
	        ,{'PID':'21','hillromId':'19','dob':'1990-05-08','gender':'Male','name':'Joseph taylor','email':'josephtaylor@gmail.com','hospital':'xyz hospital'}
	        ,{'PID':'21','hillromId':'20','dob':'1990-05-08','gender':'Male','name':'David Jones','email':'davijones@gmail.com','hospital':'abc hospital'}];
	      } 
	    }
    };
  });

