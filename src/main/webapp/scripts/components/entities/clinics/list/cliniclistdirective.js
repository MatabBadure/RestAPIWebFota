'use strict';

angular.module('hillromvestApp')
.directive('clinicList', function(ClinicService) {
  return {
    templateUrl: 'scripts/components/entities/clinics/list/list.html',
    restrict: 'E',
    scope: {
      onSelect: '&',
      onCreate: '&'
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

      $scope.selectClinic = function(clinic) {
        $scope.clinic = clinic;
        if (clinic.parent) {
          $scope.clinic.type = 'parent';
        } else {
          $scope.clinic.type = 'child';
        }
        $scope.onSelect({'clinic': clinic});
      };

      $scope.createClinic = function(){
          $scope.onCreate();
      };
    }
  }
});
