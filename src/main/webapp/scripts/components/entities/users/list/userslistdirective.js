'use strict';
/**
* @ngdoc directive
* @name userList
*
* @description
* User List  Directive To List all the User and Select one for Disassociate or Edit
*/
angular.module('hillromvestApp')
  .directive('userList', function () {
    return {
      templateUrl: 'scripts/components/entities/users/list/list.html',
      restrict: 'E',
      scope: {
        onSelect: '&',
        onCreate: '&'
      },
      link: function (scope) {
        var user = scope.user;
      },
      controller: function ($scope) {
        $scope.users = [];

        /**
        * @ngdoc function
        * @name selectUser
        * @description
        * Function to select the User from the List suggested on search
        */
        $scope.selectUser = function (user) {
          $scope.user = user;
          $scope.onSelect({
            'user' : user
          });
        };

        /**
        * @ngdoc function
        * @name sortList
        * @description
        * Function to Sort the List of Users
        */
        $scope.sortList = function () {
          //Todo
        };

         $scope.createUser = function(){
          $scope.onCreate();
        },

        /**
        * @ngdoc function
        * @name sortList
        * @description
        * Function to Search User on entering text on the textfield.
        */
        $scope.searchUsers = function () {
          $scope.users = usersList;
        };
      }
    };
  });
