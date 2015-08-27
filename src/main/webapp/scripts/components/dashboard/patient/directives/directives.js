'use strict';

angular.module('hillromvestApp')
.directive('patientNavbar', function() {
  return {
      templateUrl: 'scripts/components/dashboard/patient/views/navbar.html',
      restrict: 'E',
      controller: function($scope, $location) {
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

angular.module('hillromvestApp')
  .directive('caregiverList', function () {
    return {
      templateUrl: 'scripts/components/dashboard/patient/views/listCaregiver.html',
      restrict: 'E'
    }
  });

angular.module('hillromvestApp')
  .directive('caregiverCreateEdit', function () {
    return {
      templateUrl: 'scripts/components/dashboard/patient/views/createEditCaregiver.html',
      restrict: 'E'
    }
  });

angular.module('hillromvestApp')
  .directive('patientGraph', function() {
    return {
        templateUrl: 'scripts/components/dashboard/patient/views/graph.html',
        restrict: 'E',
      }
  });
