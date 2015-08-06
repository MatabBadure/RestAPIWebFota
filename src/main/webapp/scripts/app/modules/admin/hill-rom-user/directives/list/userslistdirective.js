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
      templateUrl: 'scripts/app/modules/admin/hill-rom-user/directives/list/list.html',
      restrict: 'E',
      scope: {
        onSelect: '&',
        onCreate: '&',
        userStatus: '=userStatus'
      },
      link: function(scope) {
        var user = scope.user;
        scope.$on('resetList', function () {
          scope.searchUsers();
        })
      },
      controller: function($scope, $timeout, $state) {
        $scope.init = function() {
          $scope.users = [];
          $scope.currentPageIndex = 1;
          $scope.perPageCount = 10;
          $scope.pageCount = 0;
          $scope.total = 0;
          $scope.sortOption ="";
          $scope.showModal = false;
        };


        var timer = false;
        $scope.$watch('searchItem', function () {
          if (timer) {
            $timeout.cancel(timer)
          }
          timer= $timeout(function () {
              $scope.searchUsers();
          },1000)
        });

        /**
         * @ngdoc function
         * @name selectUser
         * @description
         * Function to select the User from the List suggested on search
         */
        $scope.selectUser = function(user) {
          $state.go('hillRomUserEdit', { userId: user.id });
        };

        $scope.createUser = function() {
          $state.go('hillRomUserNew');
        };

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
            else if (track === "NEXT" && $scope.currentPageIndex < $scope.pageCount)
            {
              $scope.currentPageIndex++;
            }else{
              return false;
            }
          }else {
            $scope.currentPageIndex = 1;
          }
          var url = 'api/user/search?searchString=';
          UserService.getUsers(url, $scope.searchItem, $scope.sortOption, $scope.currentPageIndex, $scope.perPageCount).then(function(response) {
            $scope.users = response.data;
            $scope.total = response.headers()['x-total-count'];
            $scope.pageCount = Math.ceil($scope.total / 10);
          }).catch(function(response) {

          });
        };

        $scope.init();
      }
    };
  });
