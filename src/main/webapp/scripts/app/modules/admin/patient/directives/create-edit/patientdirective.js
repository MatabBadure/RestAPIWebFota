'use strict';

angular.module('hillromvestApp')
  .directive('patient', function(UserService, DoctorService, patientService) {
    return {
      templateUrl: 'scripts/app/modules/admin/patient/directives/create-edit/create.html',
      restrict: 'E',
      scope: {
        patient: '=patientData',
        onSuccess: '&',
        patientStatus: '=patientStatus'
      },

      controller: function($scope, noty, $state, $timeout) {

        $scope.open = function () {
          $scope.showModal = true;
        };

        $scope.close = function () {
          $scope.showModal = false;
        };
        $scope.submitted = false;
        $scope.formSubmit = function() {
          $scope.submitted = true;
        }

        $scope.init = function() {
          $scope.states = [];
          $scope.isAssociateDoctor = false;
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
          /*Get List of HCP available in the clinic*/
          $scope.getDoctors();
          $scope.getDoctorsLinkedToPatient();
        }

        $scope.loadDoctors = function($query) {
          return $scope.doctors.filter(function (doctor){
            return doctor.firstName.toLowerCase().indexOf($query.toLowerCase()) != -1;
          });
        };

        $scope.getDoctorsLinkedToPatient = function(){
          $scope.doctorsLinkedToPatient = doctorsLinkedToPatient.hcpUsers;
          /*patientService.getDoctorsLinkedToPatient($scope.patient.id).then(function(response) {
              $scope.doctorsLinkedToPatient = response.data;
            }).catch(function(response) {
            });*/
        };

        $scope.getDoctors = function() {
          var timer = false;
          var filters = [{'id':1},{'id':2}];
          timer = $timeout(function() {
            $scope.doctors = doctorsAvailInClinic.hcpUsers;
            /*DoctorService.getDoctorsInClinic(filters).then(function(response) {
              $scope.doctors = response.data;
            }).catch(function(response) {});*/
          }, 1000)
        };

        $scope.init();

        $scope.associateDoctorOption = function(){
          $scope.isAssociateDoctor = true;
        }

        $scope.disassociateDoctor = function(index){
          $scope.doctorsLinkedToPatient = doctorsLinkedToPatient.hcpUsers;
          $scope.doctorsLinkedToPatient.splice(index, 1);;
          patientService.disassociateDoctorFromPatient(id).then(function(response) {
              if (response.status == '200')
              {
                $scope.patientStatus.isMessage = true;
                $scope.patientStatus.message = response.data.message;
                $scope.isAssociateDoctor = false;
                //$scope.doctorsLinkedToPatient.delete({id : id });
                noty.showNoty({
                  text: $scope.patientStatus.message,
                  ttl: 5000,
                  type: "success"
                });
              } else{
                $scope.patientStatus.message = response.data.message;
                noty.showNoty({
                  text: $scope.patientStatus.message,
                  ttl: 5000,
                  type: "warning"
                });
              }
            }).catch(function(response) {
                $scope.patientStatus.message = response.data.message;
                noty.showNoty({
                  text: $scope.patientStatus.message,
                  ttl: 5000,
                  type: "warning"
                });
            });
        };

        $scope.associateDoctor = function() {
          if($scope.hcpForm.doctorAutoComplete.$error.minTags){
            return false;
          }
          var data = [];
          angular.forEach($scope.patient.doctors, function(doctor) {
            data.push({'id':doctor.id});
            $scope.doctorsLinkedToPatient.push(doctor);
          });
          patientService.associateHCPToPatient(data,$scope.patient.id).then(function(response){
            if (response.status == '200')
              {
                $scope.patientStatus.isMessage = true;
                $scope.patientStatus.message = response.data.message;
                noty.showNoty({
                  text: $scope.patientStatus.message,
                  ttl: 5000,
                  type: "success"
                });
              } else{
                $scope.patientStatus.message = response.data.message;
                noty.showNoty({
                  text: $scope.patientStatus.message,
                  ttl: 5000,
                  type: "warning"
                });
              }
          }).catch(function(response){
            $scope.patientStatus.isMessage = true;
            if (response.data.message !== undefined) {
              $scope.patientStatus.message = response.data.message;
            }else if(response.data.ERROR !== undefined){
              $scope.patientStatus.message = response.data.ERROR;
            } else {
              $scope.patientStatus.message = 'Error occured! Please try again';
            }
            noty.showNoty({
              text: $scope.patientStatus.message,
              ttl: 5000,
              type: "warning"
            });
          })
        };

    $scope.createPatient = function () {

      if($scope.form.$invalid){
        return false;
      }
      if($scope.patientStatus.editMode){
              // edit patient section

              var data = $scope.patient;
              data.role = 'PATIENT';

              UserService.editUser($scope.patient,data).then(function (response) {
                if(response.status == '200')
                {
                  $scope.patientStatus.isMessage = true;
                  $scope.patientStatus.message = "Patient updated successfully";
                  noty.showNoty({
                    text: $scope.patientStatus.message,
                    ttl: 5000,
                    type: "success"
                  });

                }else{
                  $scope.patientStatus.message = 'Error occured! Please try again';
                  noty.showNoty({
                    text: $scope.patientStatus.message,
                    ttl: 5000,
                    type: "warning"
                  });
                }
                $scope.reset();
              }).catch(function (response) {
                $scope.patientStatus.isMessage = true;
                if (response.data.message !== undefined) {
                $scope.patientStatus.message = response.data.message;
                }else if(response.data.ERROR !== undefined){
                  $scope.patientStatus.message = response.data.ERROR;
                } else {
                  $scope.patientStatus.message = 'Error occured! Please try again';
                }
                noty.showNoty({
                  text: $scope.patientStatus.message,
                  ttl: 5000,
                  type: "warning"
                });
              });

            }else{
              // create patient section
              var data = $scope.patient;
              data.role = 'PATIENT';

              UserService.createUser(data).then(function (response) {
                if(response.status == '201')
                {
                  $scope.patientStatus.isMessage = true;
                  // $scope.patientStatus.message = "Patient created successfully"+" with ID "+response.data.user.id;
                  $scope.patientStatus.message = "Patient created successfully";
                  noty.showNoty({
                    text: $scope.patientStatus.message,
                    ttl: 5000,
                    type: "success"
                  })

                  $scope.patientStatus.editMode = false;
                  $scope.patientStatus.isCreate = false;
                }else{
                  $scope.patientStatus.message = 'Error occured! Please try again';
                }
                $scope.reset();

              }).catch(function (response) {
                $scope.patientStatus.isMessage = true;
                if(response.status == '400' && response.data.message == "HR Id already in use."){
                  $scope.patientStatus.message = 'Patient ID ' + $scope.patient.HRID + " already in use.";
                }
                else if(response.data.ERROR !== undefined){
                  $scope.patientStatus.message = response.data.ERROR;
                }else {
                  $scope.patientStatus.message = 'Error occured! Please try again';
                }

                noty.showNoty({
                  text: $scope.patientStatus.message,
                  ttl: 5000,
                  type: "warning"
                })

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

        $scope.addHCP = function(){

        }

        $scope.deletePatient = function() {
          UserService.deleteUser($scope.patient.id).then(function(response) {
            $scope.showModal = false;
            $scope.patientStatus.isMessage = true;
            $scope.patientStatus.message = response.data.message;
            noty.showNoty({
              text: $scope.patientStatus.message,
              ttl: 5000,
              type: "success"
            });
            $scope.reset();
          }).catch(function(response) {
            $scope.patientStatus.isMessage = true;
            $scope.showModal = false;
            if (response.data.message !== undefined) {
              $scope.patientStatus.message = response.data.message;
            }else if(response.data.ERROR !== undefined){
               $scope.patientStatus.message = response.data.ERROR;
            } else {
              $scope.patientStatus.message = 'Error occured! Please try again';
            }
            noty.showNoty({
              text: $scope.patientStatus.message,
              ttl: 5000,
              type: "warning"
            });
          });
        };
        $scope.cancel = function(){
          $scope.reset();
        };

        $scope.reset = function(){
          $scope.patientStatus.editMode = false;
          $scope.patientStatus.isCreate = false;
          $scope.submitted = false;
          $scope.patient = {};
          $scope.form.$setPristine();
          $state.go('patientUser');
        }

        $scope.getAge = function(selectedDate) {
          var currentDate = new Date();
          var selectedDate = selectedDate;
          var years = currentDate.getFullYear() - selectedDate.getFullYear();
          var diff = currentDate.getTime() - selectedDate.getTime();
          var age = 0;
          age = years;
          if(years == 0){
            age = 1;
          }
          if (diff < 0) {
            age = 0;
          };
          return age;
        }

        angular.element('#dp2').datepicker({
          endDate: '+0d',
          autoclose: true}).
          on('changeDate', function(ev) {
          var selectedDate = angular.element('#dp2').datepicker("getDate");
          var _month = (selectedDate.getMonth()+1).toString();
          _month = _month.length > 1 ? _month : '0' + _month;
          var _day = (selectedDate.getDate()).toString();
          _day = _day.length > 1 ? _day : '0' + _day;
          var _year = (selectedDate.getFullYear()).toString();
          var dob = _month+"/"+_day+"/"+_year;
          $scope.patient.dob = dob;
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


var ModalInstanceCtrl = function ($scope, $modalInstance, items, selected) {

  $scope.items = items;
  $scope.selected = {
    item: selected || items[0]
  };

  $scope.ok = function () {
    $modalInstance.close($scope.selected.item);
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };
};
