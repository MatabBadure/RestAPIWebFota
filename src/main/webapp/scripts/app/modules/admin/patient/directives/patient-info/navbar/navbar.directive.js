'use strict';

angular.module('hillromvestApp')
.directive('adminPatientNavbar', function() {
  return {
      templateUrl: 'scripts/app/modules/admin/patient/directives/patient-info/navbar/navbar.html',
      restrict: 'E'
    }
});