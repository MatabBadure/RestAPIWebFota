'use strict';

angular.module('hillromvestApp')
.directive('clinicList', function() {
  return {
    templateUrl: 'scripts/components/entities/clinics/list/list.html',
    restrict: 'E',
    scope: {
      onSelect: '&'
    },
    link: function(scope, element, attrs) {
      var clinic = scope.clinic;
    },
    controller: function($scope) {
      $scope.clinics = [];

      $scope.selectClinic = function(clinic) {
        $scope.clinic = clinic;
        $scope.onSelect({'clinic': clinic});
      },

      $scope.searchClinics = function() {
        $scope.clinics = [{
          'clinicName':'Apollo Hospital.',
          'address': 'New York',
          'zip': '213321',
          'city':'New York',
          'state':'New York',
          'fax':'3423434434',
          'admin': 'Manipal'
        }, {
          'clinicName':'Apollo Hospital.',
          'address': 'New York',
          'zip': '213321',
          'city':'New York',
          'state':'New York',
          'fax':'3423434434',
          'admin': 'Manipal'
        }, {
          'clinicName':'Apollo Hospital.',
          'address': 'New York',
          'zip': '213321',
          'city':'New York',
          'state':'New York',
          'fax':'3423434434',
          'admin': 'Manipal'
        }];
      }
    }
  };
});
