'use strict';

angular.module('hillromvestApp')
  .controller('UsersController', function ($scope) {
    $scope.user = {};
    $scope.isCreate = true;
    $scope.selectedUser = function (user) {
      $scope.isCreate = false;
      $scope.user = user;
    };
  });
