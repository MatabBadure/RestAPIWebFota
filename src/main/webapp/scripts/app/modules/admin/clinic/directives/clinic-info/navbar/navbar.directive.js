'use strict';

angular.module('hillromvestApp')
.directive('clinicNavbar', function() {
  return {
      templateUrl: 'scripts/app/modules/admin/clinic/directives/clinic-info/navbar/navbar.html',
      restrict: 'E'
    }
});