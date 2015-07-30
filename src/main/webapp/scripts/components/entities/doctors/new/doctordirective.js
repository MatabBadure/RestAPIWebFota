'use strict';
angular.module('hillromvestApp')
  .directive('doctor', function (UserService, ClinicService) {
    return {
      templateUrl: 'scripts/components/entities/doctors/new/create.html',
      restrict: 'E',
      scope: {
        doctor: '=doctorData',
        doctorStatus: '=doctorStatus'
      },
      controller: function ($scope, $timeout) {

        $scope.init = function () {
          $scope.states = [];
          $scope.doctor.titles = ["Dr"];
          $scope.doctor.clinics = [{'name':'', 'id' : '' }];
          $scope.submitted = false;
          UserService.getState().then(function (response) {
            $scope.states = response.data.states;
          }).catch(function (response) {

          });
        };

        $scope.getClinics = function () {
          var timer = false;
          timer = $timeout(function () {
            ClinicService.getAllClinics().then(function (response) {
              $scope.clinics = response.data;
            }).catch(function (response) {

            });
          },1000)
        };

        $scope.selectClinic = function(clinic, index) {
          $scope.doctor.clinics[index].name = clinic.name;
          $scope.doctor.clinics[index].id = clinic.id;
          $scope.clinics = [];
        };

        $scope.formSubmit = function () {
          $scope.submitted = true;
        };

        $scope.init();

        $scope.addClinic = function () {
          $scope.doctor.clinics.push({'name':'', 'id': '' });
        };

        $scope.createDoctor = function () {
          if ($scope.form.$invalid) {
            return false;
          }
          if ($scope.doctorStatus.editMode) {
            // $scope.doctor.clinicList = [ { 'id' : 1} ];
            UserService.editUser($scope.doctor).then(function (response) {
              $scope.doctorStatus.isMessage = true;
              $scope.doctorStatus.message = response.data.message;
              $scope.reset();
            }).catch(function (response) {
              $scope.doctorStatus.isMessage = true;
             if (response.data.message !== undefined) {
              $scope.doctorStatus.message = response.data.message;
              }else if(response.data.ERROR !== undefined){
                $scope.doctorStatus.message = response.data.ERROR;
              } else {
                $scope.doctorStatus.message = 'Error occured! Please try again';
              }
            });
          } else {
            // create doctor section
            var data = $scope.doctor;
            data.role = 'HCP';

            UserService.createUser(data).then(function (response) {
              $scope.doctorStatus.isMessage = true;
              $scope.doctorStatus.message = "Doctor created successfully" + " with ID " + response.data.user.id;
              $scope.reset();
            }).catch(function (response) {
              if (response.data.message !== undefined) {
              $scope.doctorStatus.message = response.data.message;
              }else if(response.data.ERROR !== undefined){
                $scope.doctorStatus.message = response.data.ERROR;
              } else {
                $scope.doctorStatus.message = 'Error occured! Please try again';
              }
            });
          }
        };

        $scope.deleteDoctor = function () {
          UserService.deleteUser($scope.doctor.id).then(function (response) {
            $scope.doctorStatus.isMessage = true;
            $scope.doctorStatus.message = response.data.message;
            $scope.reset();
          }).catch(function (response) {
            $scope.doctorStatus.isMessage = true;
            if (response.data.message !== undefined) {
            $scope.doctorStatus.message = response.data.message;
            }else if(response.data.ERROR !== undefined){
              $scope.doctorStatus.message = response.data.ERROR;
            } else {
              $scope.doctorStatus.message = 'Error occured! Please try again';
            }
          });
        };
        $scope.cancel = function(){
          $scope.doctorStatus.editMode = false;
          $scope.doctorStatus.isCreate = false;
          $scope.reset();
        };

        $scope.reset = function(){
          $scope.doctorStatus.editMode = false;
          $scope.doctorStatus.isCreate = false;
          $scope.submitted = false;
          $scope.doctor = {};
          $scope.doctor.clinics = [];
          $scope.doctor.clinics.push({'name': '', 'id' : ''});
          $scope.form.$setPristine();
        }
      }
    };
  });
