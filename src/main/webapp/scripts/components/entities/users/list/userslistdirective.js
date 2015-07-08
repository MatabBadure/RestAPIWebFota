'use strict';

angular.module('hillromvestApp')
  .directive('userList', function() {
    return {
      templateUrl: 'scripts/components/entities/users/list/list.html',
      restrict: 'E',
      link: function postLink(scope, element, attrs) {},
      scope: {
        onSelect: '&'
      },
      link: function(scope, element, attrs) {
        var user = scope.user;
      },
      controller: function($scope) {
        $scope.users = [];

        $scope.selectUser = function(user) {
            console.info('Selecting User', user);
            $scope.user = user;
            $scope.onSelect({'user': user});
          },
          $scope.searchUsers = function() {
            $scope.users = [{
              'title':'Mr.',
              'firstName': 'John',
              'lastName': 'Smith',
              'middleName':'MiddleName',
              'email':'email',
              'role':'role',
              'role': 'Super Admin'
            }, {
              'title':'Mr.',
              'firstName': 'James',
              'lastName': 'Williams',
              'middleName':'MiddleName',
              'email':'email',
              'role':'role',
              'role': 'Account Service'
            }, {
              'title':'Mr.',
              'firstName': 'David',
              'lastName': 'Jones',
              'middleName':'MiddleName',
              'email':'email',
              'role':'role',
              'role': 'Associates'
            }, {
              'title':'Mr.',
              'firstName': 'William',
              'lastName': 'Davis',
              'middleName':'MiddleName',
              'email':'email',
              'role':'role',
              'role': 'Super Admin'
            }, {
              'title':'Mr.',
              'firstName': 'Joseph',
              'lastName': 'Taylor',
              'middleName':'MiddleName',
              'email':'email',
              'role':'role',
              'role': 'Associates'
            }, {
              'title':'Mr.',
              'firstName': 'William',
              'lastName': 'Davis',
              'middleName':'MiddleName',
              'email':'email',
              'role':'role',
              'role': 'Account Services'
            }];
          }
      }
    };
  });
