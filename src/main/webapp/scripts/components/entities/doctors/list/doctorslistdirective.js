'use strict';

angular.module('hillromvestApp')
.directive('doctorList', function () {
  return {
    templateUrl: 'scripts/components/entities/doctors/list/list.html',
    restrict: 'E',
    scope: {
      onSelect: '&',
      onCreate: '&'
    },
    link: function(scope, element, attrs) {
      var doctor = scope.doctor;
    },
    controller: function($scope) {
      $scope.doctors =[];

      $scope.selectDoctor = function(doctor) {
        $scope.doctor = doctor;
        $scope.onSelect({'doctor': doctor});
      };

       $scope.createDoctor = function(){
          $scope.onCreate();
        };

      $scope.searchDoctors = function () {
        $scope.doctors = doctorsList;
      };
    }
  };
});