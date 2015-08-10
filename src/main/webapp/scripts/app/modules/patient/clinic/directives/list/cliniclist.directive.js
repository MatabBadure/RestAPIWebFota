'use strict';

angular.module('hillromvestApp')
.directive('patientClinicList', function() {
	return {
    	templateUrl: 'scripts/app/modules/patient/clinic/directives/list/list.html',
    	restrict: 'E',
    	controller: function($scope) {

    	}
    }
});