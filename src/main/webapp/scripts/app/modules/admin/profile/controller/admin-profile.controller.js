'use strict';

angular.module('hillromvestApp')
  .controller('adminProfileController', function ($rootScope, $scope, $state, $stateParams, $location, notyService, UserService) {


    $scope.isActive = function(tab) {
      var path = $location.path();
      if (path.indexOf(tab) !== -1) {
        return true;
      } else {
        return false;
      }
    };

    $scope.initProfile = function(adminId){
      UserService.getUser(adminId).then(function(response){
        $scope.user = response.data.user;
      }).catch(function(response){});
    };

    $scope.init = function(){
      if($state.current.name === 'adminProfile' || $state.current.name === 'editAdminProfile' ){
        $scope.initProfile(localStorage.getItem('userId'));
      }
    };

    $scope.editMode = function(){
      $state.go('editAdminProfile', {'adminId': 10});
    };

    // $scope.getAdminDetails = function(){
    // };

    $scope.switchProfileTab = function(status){
      $state.go(status);
    };

    $scope.updateProfile = function(){
      UserService.editUser($scope.user).then(function(response){
        notyService.showMessage(response.data.message, 'success');
        $state.go('adminProfile');
      }).catch(function(response){
        notyService.showMessage(response.data.message, 'warning');
      });
    };

    $scope.cancel = function(){
      $state.go('adminProfile');
    };

    $scope.init();
  });