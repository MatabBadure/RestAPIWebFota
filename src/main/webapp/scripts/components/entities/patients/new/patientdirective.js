'use strict';

angular.module('hillromvestApp')
  .directive('patient', function(UserService) {
    return {
      templateUrl: 'scripts/components/entities/patients/new/create.html',
      restrict: 'E',
      scope: {
        patient: '=patientData',
        patientStatus: '=patientStatus'
      },
      controller: function($scope) {
        $scope.submitted = false;
        $scope.formSubmit = function() {
          $scope.submitted = true;
        }

        $scope.init = function() {
          $scope.states = [];
          $scope.languages = [{
            "name": "English"
          }, {
            "name": "French"
          }];
          $scope.patient.gender = "Male";
          UserService.getState().then(function(response) {
            $scope.states = response.data.states;
          }).catch(function(response) {

          });
        }

        $scope.init();

    $scope.createPatient = function () {
      if($scope.form.$invalid){
        return false;
      }
      if($scope.patientStatus.editMode){
              // edit patient section

              var data = $scope.patient;
              data.dob = data.formatedDOB;
              data.role = 'PATIENT';
             
              UserService.editUser($scope.patient,data).then(function (response) {
                if(response.status == '200')
                {
                  $scope.patientStatus.isMessage = true;
                  $scope.patientStatus.message = "Patient updated successfully";
                }else{
                  $scope.patientStatus.message = 'Error occured! Please try again';
                }
              }).catch(function (response) {
                $scope.patientStatus.isMessage = true;
                $scope.patientStatus.message = 'Error occured! Please try again';

              });

            }else{
              // create patient section
              var data = $scope.patient;
              data.role = 'PATIENT';
              
              UserService.createUser(data).then(function (response) {
                if(response.status == '201')
                {
                  $scope.patientStatus.isMessage = true;
                  $scope.patientStatus.message = "Patient created successfully"+" with ID "+response.data.user.id;
                }else{
                  $scope.patientStatus.message = 'Error occured! Please try again';
                }

              }).catch(function (response) {
                $scope.patientStatus.isMessage = true;
                if(response.status == '400' && response.data.message == "HR Id already in use."){
                  $scope.patientStatus.message = 'Patient ID ' + $scope.patient.HRID + " already in use.";
                }
                else{
                  $scope.patientStatus.message = 'Error occured! Please try again';
                }
              });
            };
          }

        if ($scope.patientStatus.editMode) {
          var selectedDate = new Date($scope.patient.dob);
          $scope.patient.age = getAge(selectedDate);
        }
        $scope.validateADMINorASP = function() {
          if ($scope.patientStatus.editMode &&
            $scope.patientStatus.role != 'ADMIN' &&
            $scope.patientStatus.role != 'Account Services Professional') {
            return true;

          }
        }

        $scope.deletePatient = function() {
          UserService.deleteUser($scope.patient.id).then(function(response) {
            $scope.patientStatus.isMessage = true;
            $scope.patientStatus.message = response.data.message;
          }).catch(function(response) {
            $scope.isMessage = true;
            if (response.data.message != undefined) {
              $scope.patientStatus.message = response.data.message;
            }else if(response.data.ERROR != undefined){
               $scope.patientStatus.message = response.data.ERROR;
            } else {
              $scope.patientStatus.message = 'Error occured! Please try again';
            }
          });
        };

        $scope.getAge = function(selectedDate) {
          var currentDate = new Date();
          var selectedDate = selectedDate;
          var diff = currentDate - selectedDate;
          var years = Math.floor(diff / (1000 * 60 * 60 * 24 * 365));
          var age;
          age = years + 1;
          if (diff < 0) {
            age = 0;
          };
          return age;
        }
        angular.element("#dp2").datepicker({
          maxDate: new Date
        });
        angular.element('#dp2').on('changeDate', function(ev) {
          var selectedDate = new Date($(this).val());
          $scope.patient.dob = selectedDate.getMonth() + "/" + selectedDate.getDate() + "/" + selectedDate.getFullYear();
          var age = $scope.getAge(selectedDate);
          angular.element('.age').val(age);
          $scope.patient.age = age;
          if (age === 0) {
            $scope.form.$invalid = true;
          }
          angular.element("#dp2").datepicker('hide');
          $scope.$digest();
        });

      }
    };
  });
