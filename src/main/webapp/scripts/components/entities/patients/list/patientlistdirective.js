'use strict';

angular.module('hillromvestApp')
  .directive('patientList', function () {
    return {
      templateUrl: 'scripts/app/entities/patients/list/patientlist.html',
      restrict: 'E',
      link: function postLink(scope, element, attrs) {
      }
    };
  });