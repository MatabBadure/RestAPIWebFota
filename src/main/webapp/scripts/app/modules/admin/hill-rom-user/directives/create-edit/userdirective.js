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
      templateUrl: 'scripts/app/modules/admin/hill-rom-user/directives/create-edit/create.html',
      restrict: 'E',
      scope: {
        user: '=userData',
        isCreate: '=isCreate',
        onSuccess: '&',
        userStatus: '=userStatus'
      },
      controller: function ($scope, noty, $state) {

         $scope.open = function () {
          $scope.showModal = true;
        };

        $scope.close = function () {
          $scope.showModal = false;
        };

        $scope.submitted = false;
        $scope.formSubmit = function () {
          $scope.submitted = true;
        };

        $scope.validateSuperAdmin = function () {
          if ($scope.userStatus.editMode && $scope.userStatus.role !== roleEnum.ADMIN) {
            return true;
          }
        };

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
          if ($scope.userStatus.editMode) {
            //will be removed when we support multiple role
            if($scope.user.authorities){
              delete $scope.user.authorities;
            }
            UserService.editUser($scope.user).then(function (response) {
              $scope.userStatus.isMessage = true;
              $scope.userStatus.message = response.data.message;
              noty.showNoty({
                text: $scope.userStatus.message,
                ttl: 5000,
                type: "success"
              });
              $scope.reset();
            }).catch(function (response) {
              $scope.userStatus.isMessage = true;
              if (response.data.message !== undefined) {
                $scope.userStatus.message = response.data.message;
              }else if(response.data.ERROR !== undefined){
                $scope.userStatus.message = response.data.ERROR;
              } else {
                $scope.userStatus.message = 'Error occured! Please try again';
              }
              noty.showNoty({
                text: $scope.userStatus.message,
                ttl: 5000,
                type: "warning"
              });
            });
          } else {
            var data = $scope.user;
            UserService.createUser(data).then(function (response) {
              $scope.userStatus.isMessage = true;
              $scope.userStatus.message = 'User created successfully';
              noty.showNoty({
                text: $scope.userStatus.message,
                ttl: 5000,
                type: "success"
              });
              $scope.reset();

            }).catch(function (response) {
              $scope.userStatus.isMessage = true;
              if (response.data.message !== undefined) {
                $scope.userStatus.message = response.data.message;
              }else if(response.data.ERROR !== undefined){
                $scope.userStatus.message = response.data.ERROR;
              } else {
                $scope.userStatus.message = 'Error occured! Please try again';
                noty.showNoty({
                  text: $scope.userStatus.message,
                  ttl: 5000,
                  type: "warning"
                });
              }
            });
          }
        };

        /**
         * @ngdoc function
         * @name deleteUser
         * @description
         * Function to delete a User
         */
        $scope.deleteUser = function () {
          UserService.deleteUser($scope.user.id).then(function (response) {
            $scope.showModal = false;
            $scope.userStatus.isMessage = true;
            $scope.userStatus.message = response.data.message;
            noty.showNoty({
              text: $scope.userStatus.message,
              ttl: 5000,
              type: "success"
            });
            $scope.reset();
          }).catch(function (response) {
            $scope.showModal = false;
            $scope.userStatus.isMessage = true;
            if (response.data.message !== undefined) {
              $scope.userStatus.message = response.data.message;
            }else if(response.data.ERROR !== undefined){
              $scope.userStatus.message = response.data.ERROR;
            }else {
            $scope.userStatus.message = 'Error occured! Please try again';
            }
            noty.showNoty({
              text: $scope.userStatus.message,
              ttl: 5000,
              type: "warning"
            });
          });
        };

        $scope.cancel = function(){
          $scope.reset();
        };

        $scope.reset = function(){
          $scope.user = {};
          $scope.userStatus.isCreate = false;
          $scope.userStatus.editMode = false;
          $scope.form.$setPristine();
          $scope.submitted = false;
          $state.go('hillRomUser');
        }

        /**
         * @ngdoc function
         * @name editUser
         * @description
         * Function to Edit User
         */
        $scope.editUser = function () {
          UserService.editUser($scope.user).then(function (response) {
            $scope.userStatus.isMessage = true;
            $scope.userStatus.message = response.data.message;
            noty.showNoty({
              text: $scope.userStatus.message,
              ttl: 5000,
              type: "success"
            });
          }).catch(function (response) {
            $scope.userStatus.isMessage = true;
            if (response.data.message !== undefined) {
              $scope.userStatus.message = response.data.message;
            } else {
              $scope.userStatus.message = 'Error occured! Please try again';
            }
            noty.showNoty({
              text: $scope.userStatus.message,
              ttl: 5000,
              type: "warning"
            });
          });
        };
      }
    };
  });
