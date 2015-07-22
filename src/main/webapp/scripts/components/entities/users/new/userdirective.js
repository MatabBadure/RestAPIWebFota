'use strict';
/**
 * @ngdoc directive
 * @name user
 *
 * @description
 * User Directive with create, edit and delete functions
 */
angular.module('hillromvestApp')
  .directive('user', function (UserService) {
    return {
      templateUrl: 'scripts/components/entities/users/new/create.html',
      restrict: 'E',
      link: function postLink(scope, element, attrs) {
      },
      scope: {
        user: '=userData',
        isCreate: '=isCreate'
      },
      controller: function ($scope) {

        /**
        * @ngdoc function
        * @name createUser
        * @description
        * Function to create a user
        */
        $scope.createUser = function () {
          if ($scope.form.$invalid) {
            return false;
          }
          var data = $scope.user;
          UserService.createUser(data).then(function (response) {
            $scope.isMessage = true;
            $scope.message = 'User created successfully' + ' with ID ' + response.data.user.id;
          }).catch(function (response) {
            $scope.isMessage = true;
            if (response.data.message !== undefined) {
              $scope.message = response.data.message;
            } else {
              $scope.message = 'Error occured! Please try again';
            }
          });
        };

        /**
        * @ngdoc function
        * @name deleteUser
        * @description
        * Function to delete a User
        */
        $scope.deleteUser = function () {
          UserService.deleteUser($scope.user.id).then(function (response) {
            $scope.isMessage = true;
            $scope.message = response.data.message;
          }).catch(function (response) {
            $scope.isMessage = true;
            if (response.data.message !== undefined) {
              $scope.message = response.data.message;
            } else {
              $scope.message = 'Error occured! Please try again';
            }
          });
        };

        /**
        * @ngdoc function
        * @name editUser
        * @description
        * Function to Edit User
        */
        $scope.editUser = function () {
          UserService.editUser($scope.user).then(function (response) {
            $scope.isMessage = true;
            $scope.message = response.data.message;
          }).catch(function (response) {
            $scope.isMessage = true;
            if (response.data.message !== undefined) {
              $scope.message = response.data.message;
            } else {
              $scope.message = 'Error occured! Please try again';
            }
          });
        };
      }
    };
  });