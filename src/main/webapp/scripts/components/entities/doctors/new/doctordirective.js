'use strict';
angular.module('hillromvestApp')
  .directive('doctor', function(UserService, ClinicService) {
    return {
      templateUrl: 'scripts/components/entities/doctors/new/create.html',
      restrict: 'E',
      scope: {
        doctor: '=doctorData',
        doctorStatus: '=doctorStatus'
      },
      controller: function ($scope, $timeout, noty) {

        $scope.open = function () {
          $scope.showModal = true;
        };

        $scope.close = function () {
          $scope.showModal = false;
        };

        $scope.init = function () {
          $scope.states = [];
          $scope.doctor.titles = ["Dr"];
          $scope.submitted = false;
          UserService.getState().then(function(response) {
            $scope.states = response.data.states;
          }).catch(function(response) {
          });
          $scope.getParentClinics();
        };

        $scope.getParentClinics = function() {
          var timer = false;
          timer = $timeout(function() {
            ClinicService.getAllClinics('/api/clinics?filter=parent:true').then(function(response) {
              $scope.clinics = response.data;
            }).catch(function(response) {

            });
          }, 1000)
        };

        $scope.removeClinic = function(index) {
          var tmpList = angular.copy($scope.doctor.clinics);
          tmpList.splice(index, 1);
          $scope.doctor.clinics = tmpList;
        };

        $scope.selectClinic = function(clinic, index) {
          $scope.doctor.clinics[index].name = clinic.name;
          $scope.doctor.clinics[index].id = clinic.id;
          $scope.clinics = [];
        };

        $scope.formSubmit = function() {
          $scope.submitted = true;
        };

        $scope.init();


        $scope.createDoctor = function() {
          if ($scope.form.$invalid) {
            return false;
          }

          if ($scope.doctorStatus.editMode) {


            $scope.doctor.clinicList = [];
            for (var i = 0; i < $scope.doctor.clinics.length; i++) {
              $scope.doctor.clinicList.push({
                'id': $scope.doctor.clinics[i].id
              });
            }
            $scope.doctor.role = 'HCP';
            UserService.editUser($scope.doctor).then(function(response) {
              $scope.doctorStatus.isMessage = true;
              $scope.doctorStatus.message = response.data.message;
              noty.showNoty({
                text: $scope.doctorStatus.message,
                ttl: 5000,
                type: "success"
              });
              $scope.reset();
            }).catch(function(response) {
              $scope.doctorStatus.isMessage = true;
              if (response.data.message !== undefined) {
                $scope.doctorStatus.message = response.data.message;
              } else if (response.data.ERROR !== undefined) {
                $scope.doctorStatus.message = response.data.ERROR;
              } else {
                $scope.doctorStatus.message = 'Error occured! Please try again';
              }
              noty.showNoty({
                text: $scope.doctorStatus.message,
                ttl: 5000,
                type: "warning"
              });
            });
          } else {
            // create doctor section
            $scope.doctor.clinicList = [];
            for (var i = 0; i < $scope.doctor.clinics.length; i++) {
              $scope.doctor.clinicList.push({
                'id': $scope.doctor.clinics[i].id
              });
            }


            var data = $scope.doctor;
            data.role = 'HCP';
            console.log('data Object: ', data);
            UserService.createUser(data).then(function(response) {
              $scope.doctorStatus.isMessage = true;
              // $scope.doctorStatus.message = "Doctor created successfully" + " with ID " + response.data.user.id;
              $scope.doctorStatus.message = "Doctor created successfully";
              noty.showNoty({
                text: $scope.doctorStatus.message,
                ttl: 5000,
                type: "success"
              });
              $scope.reset();
            }).catch(function(response) {
              if (response.data.message !== undefined) {
                $scope.doctorStatus.message = response.data.message;
              } else if (response.data.ERROR !== undefined) {
                $scope.doctorStatus.message = response.data.ERROR;
              } else {
                $scope.doctorStatus.message = 'Error occured! Please try again';
              }
              noty.showNoty({
                text: $scope.doctorStatus.message,
                ttl: 5000,
                type: "warning"
              });
            });
          }
        };


        $scope.deleteDoctor = function () {
          UserService.deleteUser($scope.doctor.id).then(function (response) {
            $scope.showModal = false;
            $scope.doctorStatus.isMessage = true;
            $scope.doctorStatus.message = response.data.message;
            noty.showNoty({
              text: $scope.doctorStatus.message,
              ttl: 5000,
              type: "success"
            });
            $scope.reset();
          }).catch(function (response) {
            $scope.showModal = false;
            $scope.doctorStatus.isMessage = true;
            if (response.data.message !== undefined) {
              $scope.doctorStatus.message = response.data.message;
            } else if (response.data.ERROR !== undefined) {
              $scope.doctorStatus.message = response.data.ERROR;
            } else {
              $scope.doctorStatus.message = 'Error occured! Please try again';
            }

            noty.showNoty({
              text: $scope.doctorStatus.message,
              ttl: 5000,
              type: "warning"
            });
          });
        };
        $scope.cancel = function() {
          $scope.reset();
        };

        $scope.reset = function() {
          $scope.doctorStatus.editMode = false;
          $scope.doctorStatus.isCreate = false;
          $scope.submitted = false;
          $scope.doctor = {};
          $scope.doctor.clinics = [];
          $scope.form.$setPristine();
        }
      }
    };
  });
