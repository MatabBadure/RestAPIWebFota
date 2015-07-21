'use strict';

angular.module('hillromvestApp')
  .directive('userList', function () {
    return {
      templateUrl: 'scripts/components/entities/users/list/list.html',
      restrict: 'E',
      scope: {
        onSelect: '&'
      },
      link: function (scope) {
        var user = scope.user;
      },
      controller: function ($scope) {
        $scope.users = [];

        $scope.selectUser = function (user) {
          $scope.user = user;
          $scope.onSelect({
            'user' : user
          });
        };

        $scope.sortList = function () {
          console.log('Todo Sort Functionality...!');
        };

        $scope.searchUsers = function () {
          $scope.users = usersList;
        };
      }
    };
  });
