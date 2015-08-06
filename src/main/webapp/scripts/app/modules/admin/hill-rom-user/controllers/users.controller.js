'use strict';

angular.module('hillromvestApp')
  .controller('UsersController', function($scope,UserService,$state,$stateParams) {
    $scope.user = {};
    $scope.userStatus = {
      'role': localStorage.getItem('role'),
      'editMode': false,
      'isCreate': false,
      'isMessage': false
    };

    $scope.init = function() {
      var currentRoute = $state.current.name;
      if ($state.current.name === 'hillRomUserEdit') {
        $scope.getUserDetails($stateParams.userId, $scope.setEditMode);
      } else if ($state.current.name === 'hillRomUserNew') {
        $scope.createUser();
      }
    };

      $scope.setEditMode = function(user) {
        $scope.userStatus.editMode = true;
        $scope.userStatus.isCreate = false;
        $scope.user = user;

      }
      $scope.getUserDetails = function(userId, callback) {
      UserService.getUser(userId).then(function(response) {
        $scope.user = response.data;
        if (typeof callback === 'function') {
          callback($scope.user);
        }
      }).catch(function(response) {});
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
        title: hillRomUser.title,
        role: hillRomUser.role
      };
    };

    $scope.onSuccess = function() {
      $scope.$broadcast('resetList', {});
    };

    $scope.init();
  });
