'use strict';

angular.module('hillromvestApp')
    .controller('UserLoginTokenDetailController', function ($scope, $stateParams, UserLoginToken, User) {
        $scope.userLoginToken = {};
        $scope.load = function (id) {
            UserLoginToken.get({id: id}, function(result) {
              $scope.userLoginToken = result;
            });
        };
        $scope.load($stateParams.id);
    });
