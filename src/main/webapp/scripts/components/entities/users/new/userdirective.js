'use strict';

angular.module('hillromvestApp')
  .directive('user', function () {
    return {
      templateUrl: 'scripts/components/entities/users/new/create.html',
      restrict: 'E',
      link: function postLink(scope, element, attrs) {
      },
      scope: {
      },
      controller: function($scope) {
        $scope.createUser = function () {
          console.log('Inside the Controller');
          var data = {
            'title': $scope.title,
            'firstName': $scope.firstName,
            'lastName': $scope.lastName,
            'role': $scope.role,
            'email': $scope.email
          };
          console.log('data: ',data);
        }
      }
    };
  });