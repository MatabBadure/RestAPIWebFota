'use strict';

angular.module('hillromvestApp')
  .directive('clinic', function() {
    return {
      templateUrl: 'scripts/components/entities/clinics/new/create.html',
      restrict: 'E',
      scope: {
        data: '@'
      },
      link: function postLink(scope, element, attrs) {},
      controller: function($scope) {
        $scope.create = function() {

        };
      }
    };
  });
