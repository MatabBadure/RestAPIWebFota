'use strict';

angular.module('hillromvestApp')
  .directive('navBar', function($window, $location) {
    return {
      templateUrl: 'scripts/app/navbar/navbar.html',
      restrict: 'E',
      controller: function($scope) {
        $scope.isActive = function(tab) {
          var path = $location.path();
          if (path.indexOf(tab) !== -1) {
            return true;
          } else {
            return false;
          }
        };
      }
    };
  });
