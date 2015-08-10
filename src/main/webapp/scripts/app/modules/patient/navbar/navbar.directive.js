'use strict';

angular.module('hillromvestApp')
.directive('patientNavbar', function() {
  return {
      templateUrl: 'scripts/app/modules/patient/navbar/navbar.html',
      restrict: 'E',
      controller: function($scope) {

      }
    }
});