'use strict';

angular.module('hillromvestApp')
  .directive('patientList', function () {
    return {
      templateUrl: 'scripts/components/entities/patients/list/patientlist.html',
      restrict: 'E',
      link: function postLink(scope, element, attrs) {
      }
    };
  });