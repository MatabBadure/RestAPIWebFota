'use strict';

angular.module('hillromvestApp')
  .directive('doctor', function () {
    return {
      templateUrl: 'scripts/components/entities/doctors/new/create.html',
      restrict: 'E',
      link: function postLink(scope, element, attrs) {
      }
    };
  });