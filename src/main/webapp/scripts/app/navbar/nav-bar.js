'use strict';

angular.module('hillromvestApp')
  .directive('navBar', function($window) {
    return {
      templateUrl: 'scripts/app/navbar/navbar.html',
      restrict: 'E',
      link: function postLink(scope, element, attrs) {
      }
    };
  });