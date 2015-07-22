  'use strict';

  angular.module('hillromvestApp')
  .directive('doctor', function (Doctor) {
    return {
      templateUrl: 'scripts/components/entities/doctors/new/create.html',
      restrict: 'E',
      scope: {
        doctor: '=doctorData',
        doctorStatus: '=doctorStatus'
      },
      controller: function ($scope) {
        $scope.createDoctor = function () {
          if ($scope.form.$invalid) {
            return false;
          }
          var data = $scope.doctor;
          data.title = 'Dr';
          data.role = 'DOCTOR';

          Doctor.createDoctor(data).then(function (response) {
            $scope.doctorStatus.isMessage = true;
            $scope.doctorStatus.message = "Doctor created successfully" + " with ID " + response.data.user.id;
          }).catch(function (response) {
            $scope.doctorStatus.isMessage = true;
            if (response.data.message !== undefined) {
              $scope.doctorStatus.message = response.data.message;
            } else {
              $scope.doctorStatus.message = 'Error occured! Please try again';
            }
          });
        };

        $scope.deleteDoctor = function () {
          $scope.doctor.id = 1;
          Doctor.deleteDoctor($scope.doctor.id).then(function (response) {
            $scope.doctorStatus.isMessage = true;
            $scope.doctorStatus.message = response.data.message;
          }).catch(function (response) {
            $scope.doctorStatus.isMessage = true;
            if (response.data.message !== undefined) {
              $scope.doctorStatus.message = response.data.message;
            } else {
              $scope.doctorStatus.message = 'Error occured! Please try again';
            }
          });
        };
      }
    };
  });

