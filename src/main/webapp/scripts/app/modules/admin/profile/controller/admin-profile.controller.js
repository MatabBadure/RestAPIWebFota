'use strict';

angular.module('hillromvestApp')
  .controller('adminProfileController', function ($rootScope, $scope, $state, $stateParams, $location, notyService, UserService, Password, Auth, AuthServerProvider) {


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
      AuthServerProvider.getSecurityQuestions().then(function(response){
        $scope.questions = response.data
      }).catch(function(response){});
    };

    $scope.init = function(){
      if($state.current.name === 'adminProfile' || $state.current.name === 'editAdminProfile' ){
        $scope.initProfile(localStorage.getItem('userId'));
      }
    };

    $scope.editMode = function(){
      $state.go('editAdminProfile');
    };

    $scope.switchProfileTab = function(status){
      $state.go(status);
    };

    $scope.updateProfile = function(){
      $scope.submitted = true;
      if($scope.form.$invalid){
        return false;
      }
      if($scope.resetAccount){
        var data = {
          "questionId": $scope.resetAccount.question.id,
          "answer": $scope.resetAccount.answer
        };
        AuthServerProvider.changeSecurityQuestion(data, $scope.user.id).then(function(response){
          console.log(response);
        }).catch(function(response){});
      }
      UserService.editUser($scope.user).then(function(response){
        notyService.showMessage(response.data.message, 'success');
        $state.go('adminProfile');
      }).catch(function(response){
        if(response.data.message){
          notyService.showMessage(response.data.message, 'warning');
        } else if(response.data.ERROR){
          notyService.showMessage(response.data.ERROR, 'warning');
        }
      });
    };

    $scope.updatePassword = function(){
      $scope.submitted = true;
      if($scope.form.$invalid){
        return false;
      }
      var data = {
        'password': $scope.profile.password,
        'newPassword': $scope.profile.newPassword
      };
      Password.updatePassword(localStorage.getItem('userId'), data).then(function(response){
        Auth.logout();
        notyService.showMessage(response.data.message, 'success');
        $state.go('login');
      }).catch(function(response){
        notyService.showMessage(response.data.error, 'warning');
      });
    };

    $scope.cancel = function(){
      $state.go('adminProfile');
    };

    $scope.init();
  });