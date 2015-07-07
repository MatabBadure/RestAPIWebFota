'use strict';

angular.module('hillromvestApp')
  .directive('doctor', function () {
    return {
      templateUrl: 'scripts/app/entities/doctors/create.html',
      restrict: 'E',
      link: function postLink(scope, element, attrs) {
      }
    };
  });