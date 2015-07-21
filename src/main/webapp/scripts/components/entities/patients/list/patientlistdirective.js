'use strict';

angular.module('hillromvestApp')
  .directive('patientList', function () {
	    return {
	      templateUrl: 'scripts/components/entities/patients/list/patientlist.html',
	      restrict: 'E',
	      link: function postLink(scope, element, attrs) {
	      },
	      scope: {
	      onSelect: '&'
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

	      $scope.searchPatients = function(){
	        $scope.patients = [{'PID':'21','HRID':'15','gender':'Male','dob':'07/18/1990','firstName':'Johny','lastName':'Dep','name':'Johny Dep','email':'JohnyDep@gmail.com','hospital':'Appolo hospital'}
	        ,{'PID':'21','HRID':'16','dob':'07/18/1990','gender':'Male','name':'James williams','email':'JamesWilliams@gmail.com','hospital':'Manipal hospital'}
	        ,{'PID':'21','HRID':'17','dob':'07/18/1990','gender':'Male','name':'David Jones','email':'davijones@gmail.com','hospital':'abc hospital'}
	        ,{'PID':'21','HRID':'18','dob':'07/18/1990','gender':'Male','name':'William Davis','email':'williamdavis@gmail.com','hospital':'mno hospital'}
	        ,{'PID':'21','HRID':'19','dob':'07/18/1990','gender':'Male','name':'Joseph taylor','email':'josephtaylor@gmail.com','hospital':'xyz hospital'}
	        ,{'PID':'21','HRID':'20','dob':'07/18/1990','gender':'Male','name':'David Jones','email':'davijones@gmail.com','hospital':'abc hospital'}];
	      } 
	    }
    };
  });

