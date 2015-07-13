'use strict';

angular.module('hillromvestApp')
  .directive('userList', function() {
    return {
      templateUrl: 'scripts/components/entities/users/list/list.html',
      restrict: 'E',
      scope: {
        onSelect: '&'
      },
      link: function(scope) {
        var user = scope.user;
      },
      controller: function($scope) {
        $scope.users = [];

        $scope.selectUser = function(user) {
          $scope.user = user;
          $scope.onSelect({
            'user': user
          });
        };

        $scope.sortList = function() {
          console.log('Todo Sort Functionality...!');
        };

        $scope.searchUsers = function() {
          $scope.users = [{
            'title': 'Mr.',
            'firstName': 'John',
            'lastName': 'Smith',
            'middleName': 'MiddleName',
            'email': 'email',
            'role': 'Super Admin'
          }, {
            'title': 'Mr.',
            'firstName': 'James',
            'lastName': 'Williams',
            'middleName': 'MiddleName',
            'email': 'email',
            'role': 'Account Service'
          }, {
            'title': 'Mr.',
            'firstName': 'David',
            'lastName': 'Jones',
            'middleName': 'MiddleName',
            'email': 'email',
            'role': 'Associates'
          }, {
            'title': 'Mr.',
            'firstName': 'William',
            'lastName': 'Davis',
            'middleName': 'MiddleName',
            'email': 'email',
            'role': 'Super Admin'
          }, {
            'title': 'Mr.',
            'firstName': 'Joseph',
            'lastName': 'Taylor',
            'middleName': 'MiddleName',
            'email': 'email',
            'role': 'Associates'
          }, {
            'title': 'Mr.',
            'firstName': 'William',
            'lastName': 'Davis',
            'middleName': 'MiddleName',
            'email': 'email',
            'role': 'Account Services'
          }, {
            'title': 'Mr.',
            'firstName': 'William',
            'lastName': 'Davis',
            'middleName': 'MiddleName',
            'email': 'email',
            'role': 'Account Services'
          }, {
            'title': 'Mr.',
            'firstName': 'William',
            'lastName': 'Davis',
            'middleName': 'MiddleName',
            'email': 'email',
            'role': 'Account Services'
          }, {
            'title': 'Mr.',
            'firstName': 'William',
            'lastName': 'Davis',
            'middleName': 'MiddleName',
            'email': 'email',
            'role': 'Account Services'
          }, {
            'title': 'Mr.',
            'firstName': 'William',
            'lastName': 'Davis',
            'middleName': 'MiddleName',
            'email': 'email',
            'role': 'Account Services'
          }, {
            'title': 'Mr.',
            'firstName': 'William',
            'lastName': 'Davis',
            'middleName': 'MiddleName',
            'email': 'email',
            'role': 'Account Services'
          }, {
            'title': 'Mr.',
            'firstName': 'William',
            'lastName': 'Davis',
            'middleName': 'MiddleName',
            'email': 'email',
            'role': 'Account Services'
          }, {
            'title': 'Mr.',
            'firstName': 'William',
            'lastName': 'Davis',
            'middleName': 'MiddleName',
            'email': 'email',
            'role': 'Account Services'
          }, {
            'title': 'Mr.',
            'firstName': 'William',
            'lastName': 'Davis',
            'middleName': 'MiddleName',
            'email': 'email',
            'role': 'Account Services'
          }, {
            'title': 'Mr.',
            'firstName': 'William',
            'lastName': 'Davis',
            'middleName': 'MiddleName',
            'email': 'email',
            'role': 'Account Services'
          }, {
            'title': 'Mr.',
            'firstName': 'William',
            'lastName': 'Davis',
            'middleName': 'MiddleName',
            'email': 'email',
            'role': 'Account Services'
          }];
        };
      }
    };
  });
