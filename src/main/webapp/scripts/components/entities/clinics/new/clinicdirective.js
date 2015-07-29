'use strict';

angular.module('hillromvestApp')
  .directive('clinic', function(ClinicService, UserService) {
    return {
      templateUrl: 'scripts/components/entities/clinics/new/create.html',
      restrict: 'E',
      scope: {
        clinic: '=clinicData',
        clinicStatus: '=clinicStatus'
      },


      controller: function($scope) {

        $scope.init = function() {
          $scope.clinic = {};
          $scope.clinic.type = 'parent';
          UserService.getState().then(function(response) {
            $scope.states = response.data.states;
          }).catch(function(response) {

          });
        };

        $scope.init();

        $scope.newChildClinic = function() {
          $scope.clinic.childClinics.push({
            name: ''
          });
        };

        $scope.removeChildClinic = function(idx) {
          $scope.clinic.childClinics.splice(idx, 1);
        };

        $scope.submitted = false;
        $scope.formSubmit = function() {
          $scope.submitted = true;
        };

        $scope.states = [];



        $scope.createClinic = function() {
          if ($scope.form.$invalid) {
            return false;
          }
          if ($scope.clinic.type === 'parent') {
            $scope.clinic.parent = true;
          } else {
            $scope.clinic.parent = false;
          }
          if ($scope.clinic.parentClinic) {
            delete $scope.clinic.parentClinic.name;
          }
          if ($scope.clinicStatus.editMode) {
            // edit Clinic section
            var data = $scope.clinic;

            ClinicService.updateClinic(data).then(function(data) {
              $scope.clinicStatus.isMessage = true;
              $scope.clinicStatus.message = "Clinic updated successfully" + " with ID " + data.data.Clinic.id;
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
            if ($scope.clinic.type === 'parent' && $scope.clinic.parentClinic) {
              delete $scope.clinic.parentClinic;
            } else {
              for (var i = 0; i < clinicsList.length; i++) {
                if ($scope.clinic.parentClinic && clinicsList[i].name === $scope.clinic.parentClinic.name) {
                  $scope.clinic.parentClinic.id = clinicsList[i].id;
                }
              }
            }
            // create clinic section
            var data = $scope.clinic;

            ClinicService.createClinic(data).then(function(data) {
              $scope.clinicStatus.isMessage = true;
              $scope.clinicStatus.message = "Clinic created successfully" + " with ID " + data.data.Clinic.id;
              $scope.clinic = "";
              $scope.clinicStatus.editMode = false;
              $scope.clinicStatus.isCreate = false;
            }).catch(function(response) {
              if (response.data.message !== undefined) {
                $scope.clinicStatus.message = response.data.message;
              } else {
                $scope.clinicStatus.message = 'Error occured! Please try again';
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

        $scope.getParentClinic = function() {
          $scope.clinics = clinicsList;
        };

        $scope.selectClinic = function(clinic) {
          $scope.clinic.parentClinic.name = clinic.name;
          $scope.clinics = [];
        };

        $scope.removeParent = function() {
          $scope.clinic.parentClinic = null;
        };
      }
    };
  });
