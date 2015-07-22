'use strict';
/**
 * @ngdoc directive
 * @name doctor
 *
 * @description
 * Doctor directive to create, edit and delete Doctor
 */
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

        /**
        * @ngdoc function
        * @name createDoctor
        * @description
        * function to create a doctor
        */
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

        /**
        * @ngdoc function
        * @name deleteDoctor
        * @description
        * Function to delete a doctor
        */
        $scope.deleteDoctor = function () {
          //'$scope.doctor.id = 1;' Will be Removed after get REST API is up and runnig
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

        /**
        * @ngdoc function
        * @name editDoctor
        * @description
        * Function to edit a doctor
        */
        $scope.editDoctor = function () {
          Doctor.editDoctor($scope.doctor).then(function (response) {
            $scope.doctorStatus.isMessage = true;
            $scope.doctorStatus.message = response.data.message;
          }).cathc(function (response) {
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
