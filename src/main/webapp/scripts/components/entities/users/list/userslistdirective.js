'use strict';
/**
 * @ngdoc directive
 * @name userList
 *
 * @description
 * User List  Directive To List all the User and Select one for Disassociate or Edit
 */
angular.module('hillromvestApp')
  .directive('userList', function(UserService) {
    return {
      templateUrl: 'scripts/components/entities/users/list/list.html',
      restrict: 'E',
      scope: {
        onSelect: '&',
        onCreate: '&'
      },
      link: function(scope) {
        var user = scope.user;
      },
      controller: function($scope) {
        $scope.init = function() {
          $scope.users = [];
          $scope.currentPageIndex = 1;
          $scope.perPageCount = 10;
          $scope.pageCount = 0;
          $scope.total = 0;
        };

        $scope.init();

        /**
         * @ngdoc function
         * @name selectUser
         * @description
         * Function to select the User from the List suggested on search
         */
        $scope.selectUser = function(user) {
          UserService.getUser(user.id).then(function(response) {
            $scope.user = response.data;
            $scope.onSelect({
              'user': user
            });
          }).catch(function(response) {

          });
        };

        /**
         * @ngdoc function
         * @name sortList
         * @description
         * Function to Sort the List of Users
         */
        $scope.sortList = function() {
          //Todo
        };

        $scope.createUser = function() {
            $scope.onCreate();
          },

          /**
           * @ngdoc function
           * @name sortList
           * @description
           * Function to Search User on entering text on the textfield.
           */
          $scope.searchUsers = function(track) {
            if (track !== undefined) {
              if (track === "PREV" && $scope.currentPageIndex > 1) {
                $scope.currentPageIndex--;
              }
              if (track === "NEXT") {
                $scope.currentPageIndex++;
              }
            }
            var url = 'api/user/search?searchString=';
            UserService.getUsers(url, $scope.searchItem, $scope.currentPageIndex, $scope.perPageCount).then(function(response) {
              $scope.users = response.data;
            }).catch(function(response) {

            });
          };
      }
    };
  });
