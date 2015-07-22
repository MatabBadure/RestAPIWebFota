'use strict';

angular.module('hillromvestApp')
.directive('clinicList', function() {
  return {
    templateUrl: 'scripts/components/entities/clinics/list/list.html',
    restrict: 'E',
    scope: {
      onSelect: '&'
    },
    link: function(scope) {
      var clinic = scope.clinic;
    },
    controller: function($scope) {
      $scope.clinics = [];
        $scope.selectClinic = function (clinic) {
          $scope.clinic = clinic;
          $scope.onSelect({
            'clinic': clinic
          });
        };

        $scope.searchClinics = function () {
          $scope.clinics = clinicsList;
        };
      }
    };
  });
