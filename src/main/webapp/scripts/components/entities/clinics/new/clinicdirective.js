  'use strict';

  angular.module('hillromvestApp')
    .directive('clinic', function(ClinicService) {
      return {
        templateUrl: 'scripts/components/entities/clinics/new/create.html',
        restrict: 'E',
        scope: {
          clinic: '=clinicData',
          clinicStatus: '=clinicStatus'
        },
        link: function postLink(scope, element, attrs) {},

        controller: function($scope) {
          $scope.init = function() {
            $scope.clinic = {};
            $scope.clinic.childClinics = [{
              name: ''
            }];
          }

          $scope.init();
          $scope.newChildClinic = function() {
            $scope.clinic.childClinics.push({
              name: ''
            });
          };

          $scope.removeChildClinic = function(idx) {
            $scope.clinic.childClinics.splice(idx, 1);
          }

          $scope.clinicFormSubmit = function() {
            if ($scope.form.$invalid) {
              return false;
            }
            if ($scope.clinicStatus.isCreate) {
              $scope.clinic.hillromId = null;
              var data = $scope.clinic;

              ClinicService.createClinic(data).then(function(data) {
                $scope.clinicStatus.isMessage = true;
                $scope.clinicStatus.message = "Clinic created successfully" + " with ID " + data.data.ParentClinic.id;
                $scope.init();
                $scope.form.$setPristine();
              }).catch(function(response) {
                if (response.data.message !== undefined) {
                  $scope.clinicStatus.message = response.data.message;
                } else {
                  $scope.clinicStatus.message = 'Error occurred! Please try again';
                }
                $scope.clinicStatus.isMessage = true;
              });
            } else {
              $scope.clinic.hillromId = null;
              var data = $scope.clinic;
              ClinicService.updateClinic(data).then(function(data) {
                $scope.clinicStatus.isMessage = true;
                $scope.clinicStatus.message = "Clinic updated successfully" + " with ID " + data.data.ParentClinic.id;
                $scope.init();
                $scope.form.$setPristine();
              }).catch(function(response) {
                if (response.data.message !== undefined) {
                  $scope.clinicStatus.message = response.data.message;
                } else {
                  $scope.clinicStatus.message = 'Error occurred! Please try again';
                }
                $scope.clinicStatus.isMessage = true;
              });
            }
          };

          $scope.deleteClinic = function() {
            $scope.clinic.id = 1;
            ClinicService.deleteClinic($scope.clinic.id).then(function(data) {
              $scope.clinicStatus.isMessage = true;
              $scope.clinicStatus.message = data.data.message;
            }).catch(function(response) {
              if (response.data.message !== undefined) {
                $scope.clinicStatus.message = data.data.message;
              } else {
                $scope.clinicStatus.message = 'Error occured! Please try again';
              }
              $scope.clinicStatus.isMessage = true;
            });
          };
        }
      };
    });
