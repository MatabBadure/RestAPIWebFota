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
	        $scope.patients = [{'firstName':'Johny','lastName':'Dep','name':'Johny Dep','email':'JohnyDep@gmail.com','hospital':'Appolo hospital'}
	        ,{'name':'James williams','email':'JamesWilliams@gmail.com','hospital':'Manipal hospital'}
	        ,{'name':'David Jones','email':'davijones@gmail.com','hospital':'abc hospital'}
	        ,{'name':'William Davis','email':'williamdavis@gmail.com','hospital':'mno hospital'}
	        ,{'name':'Joseph taylor','email':'josephtaylor@gmail.com','hospital':'xyz hospital'}
	        ,{'name':'David Jones','email':'davijones@gmail.com','hospital':'abc hospital'}];
	      } 
	    }
    };
  });