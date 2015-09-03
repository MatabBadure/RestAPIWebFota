'use strict';

angular.module('hillromvestApp')
.directive('patientProfileNavbar', function() {
  return {
      templateUrl: 'scripts/app/modules/patient/profile/navbar/navbar.html',
      restrict: 'E',
      controller: function ($scope, $location) {
        $scope.isActive = function(tab) {
          var path = $location.path();
          if (path.indexOf(tab) !== -1) {
            return true;
          } else {
            return false;
          }
        };
      }
    }
});