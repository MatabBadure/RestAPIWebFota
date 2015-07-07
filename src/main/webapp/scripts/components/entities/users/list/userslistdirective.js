'use strict';

angular.module('hillromvestApp')
  .directive('userList', function () {
    return {
      templateUrl: 'scripts/components/entities/users/list/list.html',
      restrict: 'E',
      link: function postLink(scope, element, attrs) {
      },
      scope: {
        scope: '='
      },
      controller: function($scope) {
        $scope.users = [{'name':'John Smith','role':'Super Admin'},{'name':'James Williams','role':'Account Service'},{'name':'David Jones','role':'Associates'},{'name':'William Davis','role':'Super Admin'},{'name':'Joseph Taylor','role':'Associates'},{'name':'William Davis','role':'Account Services'}];

        $scope.selectUser = function (user) {
          console.log('Selected User: ', user);
        }
      }
    };
  });