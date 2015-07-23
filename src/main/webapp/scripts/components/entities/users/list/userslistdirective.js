'use strict';

angular.module('hillromvestApp')
  .directive('userList', function (UserService) {
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

        $scope.selectUser = function (user) {
          $scope.user = user;
          $scope.onSelect({
            'user' : user
          });
        };

         $scope.createUser = function(){
          $scope.onCreate();
        },

        $scope.sortList = function () {
          console.log('Todo Sort Functionality...!');
        };

        $scope.searchUsers = function () {
          $scope.users = usersList;
          UserService.getUsers($scope.searchItem, 1, 10).then(function (response) {
            console.log('SUCCESS RESPONSE',response);
          }).catch(function (response){
            console.log('ERROR RESPONSE',response);
          });
        };
      }
    };
  });
