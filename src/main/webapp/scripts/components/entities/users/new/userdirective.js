'use strict';

angular.module('hillromvestApp')
  .directive('user', function (User) {
    return {
      templateUrl: 'scripts/components/entities/users/new/create.html',
      restrict: 'E',
      link: function postLink(scope, element, attrs) {
      },
      scope: {
        user:'=userData',
        isCreate:'=isCreate'
      },
      controller: function ($scope) {
        $scope.createUser = function () {
          var data = {
            'title': $scope.user.title,
            'firstName': $scope.user.firstName,
            'middleName': $scope.user.middleName,
            'lastName': $scope.user.lastName,
            'role': $scope.user.role,
            'email': $scope.user.email
          };
          User.createUser(data).then(function (data) {
            $scope.user = {};
          }).catch(function () {

          });
        };

        $scope.deleteUser = function(){
          User.deleteUser($scope.user.id).then(function (data) {
            $scope.user = {};
          }).catch(function () {
            console.log('Giving Error');
          });
        };
      }
    };
  });