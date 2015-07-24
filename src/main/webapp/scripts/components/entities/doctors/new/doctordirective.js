'use strict';

angular.module('hillromvestApp')
.directive('doctor', function (UserService) {
  return {
    templateUrl: 'scripts/components/entities/doctors/new/create.html',
    restrict: 'E',
    scope: {
      doctor: '=doctorData',
      doctorStatus: '=doctorStatus'
    },
    controller: function ($scope) {

      var init = function(){
        $scope.states =[];
        $scope.doctor.title ="Mr.";
        $scope.submitted = false;

        UserService.getState().then(function (response) {
          $scope.states = response.data.states;
        }).catch(function (response) {
          console.log("getState call failed!");
        });
        $scope.formSubmit = function(){
          $scope.submitted = true;
        }
      }

      init();

      $scope.createDoctor = function () {
        if ($scope.form.$invalid) {
          return false;
        }
        if($scope.doctorStatus.editMode){
          //Edit doctor section
        }else{
          // create doctor section
          var data = $scope.doctor;
        data.title = 'Dr';
        data.role = 'DOCTOR';

        UserService.createUser(data).then(function (response) {
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
        }
        
      };

      $scope.deleteDoctor = function () {
        $scope.doctor.id = 1;
        UserService.deleteUser($scope.doctor.id).then(function (response) {
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

