'use strict';

angular.module('hillromvestApp')
  .directive('user', function () {
    return {
      templateUrl: 'scripts/components/entities/users/new/create.html',
      restrict: 'E',
      link: function postLink(scope, element, attrs) {
      },
      scope: {
        user:'=userData'
      },
      controller: function ($scope) {
        $scope.createUser = function () {
          console.log('Inside the Controller');
          var data = {
            'title': $scope.user.title,
            'firstName': $scope.user.firstName,
            'middleName': $scope.user.middleName,
            'lastName': $scope.user.lastName,
            'role': $scope.user.role,
            'email': $scope.user.email
          };
          console.log('data: ',data);
        }
      }
    };
  });