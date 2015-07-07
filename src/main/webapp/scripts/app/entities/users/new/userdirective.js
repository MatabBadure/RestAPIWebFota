'use strict';

angular.module('hillromvestApp')
  .directive('user', function () {
    return {
      templateUrl: 'scripts/app/entities/users/create.html',
      restrict: 'E',
      link: function postLink(scope, element, attrs) {
      },
      scope: {
      },
      controller: function($scope) {
        $scope.createUser = function () {
          console.log('Inside the Controller');
        }
      }
    };
  });