'use strict';

angular.module('hillromvestApp')
.directive('doctorList', function (UserService) {
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
        console.info(doctor.id);
        UserService.getUser(doctor.id).then(function (response) {
          response.data.clinicList = [ { 'id': 1, 'name': 'Name1'}, { 'id': 2, 'name': 'Name2'}];
          console.log('Complete Object: ', $scope.doctor);
          $scope.doctor = response.data;
          $scope.onSelect({'doctor': doctor});
        }).catch(function (response) {

        });
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