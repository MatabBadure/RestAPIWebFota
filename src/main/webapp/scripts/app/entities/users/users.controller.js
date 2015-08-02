'use strict';

angular.module('hillromvestApp')
  .controller('UsersController', function($scope) {
    $scope.user = {};
    $scope.userStatus = {
      'role': localStorage.getItem('role'),
      'editMode': false,
      'isCreate': false,
      'isMessage': false
    };

    $scope.selectedUser = function(user) {
      $scope.userStatus.isCreate = false;
      $scope.userStatus.editMode = true;
      $scope.user = user;
    };

    $scope.createUser = function() {
      $scope.userStatus.isCreate = true;
      $scope.userStatus.isMessage = false;
      $scope.user = {
        title: 'Mr.'
      };
    };

    $scope.onSuccess = function() {
      $scope.$broadcast('resetList', {});
    };
  });
