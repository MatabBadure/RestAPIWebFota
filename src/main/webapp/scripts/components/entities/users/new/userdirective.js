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
          if($scope.form.$invalid){
            return false;
          }
          var data = {
            'title': $scope.user.title,
            'firstName': $scope.user.firstName,
            'middleName': $scope.user.middleName,
            'lastName': $scope.user.lastName,
            'role': $scope.user.role,
            'email': $scope.user.email
          };
          User.createUser(data).then(function (response) {
            $scope.isMessage = true;  
            $scope.message = "User created successfully"+" with ID "+response.data.user.id;
          }).catch(function (response) {
            $scope.isMessage = true;  
            if(response.data.message != undefined){
              $scope.message = response.data.message;
            }else{
              $scope.message = 'Error occured! Please try again';
           }
          });
        };

        $scope.deleteUser = function(){
          User.deleteUser($scope.user.id).then(function (response) {
            $scope.isMessage = true;  
            $scope.message = response.data.message;
          }).catch(function (response) {
            $scope.isMessage = true;  
            if(response.data.message != undefined){
              $scope.message = response.data.message;
            }else{
              $scope.message = 'Error occured! Please try again';
           }
          });
        };
      }
    };
  });