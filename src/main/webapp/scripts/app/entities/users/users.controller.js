'use strict';

angular.module('hillromvestApp')
  .controller('UsersController', function ($scope) {
    $scope.user = {};
    $scope.usersList = [];

    $scope.selectedUser = function(user) {
      console.info(user, 'controller');
      $scope.user = user;
    };

  });
