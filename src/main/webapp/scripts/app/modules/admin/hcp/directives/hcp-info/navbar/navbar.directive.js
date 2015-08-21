'use strict';

angular.module('hillromvestApp')
.directive('hcpNavbar', function() {
  return {
      templateUrl: 'scripts/app/modules/admin/hcp/directives/hcp-info/navbar/navbar.html',
      restrict: 'E'
    }
});