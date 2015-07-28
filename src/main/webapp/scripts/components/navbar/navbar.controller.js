'use strict';

angular.module('hillromvestApp')
    .controller('NavbarController', function ($scope, $location, $state, Auth, Principal) {
        $scope.$state = $state;
        $scope.isActive = function(tab) {
          var path = $location.path();
          if (path.indexOf(tab) !== -1) {
            return true;
          } else {
            return false;
          }
        };
    });
