'use strict';

angular.module('hillromvestApp')
  .directive('patient', function () {
    return {
      templateUrl: 'scripts/components/entities/patients/new/create.html',
      restrict: 'E',
      link: function postLink(scope, element, attrs) {
      }
    };
  });