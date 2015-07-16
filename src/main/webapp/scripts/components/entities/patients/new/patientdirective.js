'use strict';

angular.module('hillromvestApp')
  .directive('patient', function (Patient) {
    return {
      templateUrl: 'scripts/components/entities/patients/new/create.html',
      restrict: 'E',
      link: function postLink(scope, element, attrs) {
      },
      scope: {
        patient:'=patientData',
        patientStatus:'=patientStatus'
      },
      controller: function ($scope) {


        $scope.createPatient = function () {
          if($scope.form.$invalid){
            return false;
          }
          var data = {
            'id': $scope.patient.HRID,
            'title': $scope.patient.title,
            'gender': $scope.patient.gender,
            'language': $scope.patient.language,
            'firstName': $scope.patient.firstName,
            'middleName': $scope.patient.middleName,
            'lastName':$scope.patient.lastName,
            'dob': $scope.patient.dob,
            'age': $scope.patient.age,
            'address': $scope.patient.address,
            'city': $scope.patient.city,
            'state': $scope.patient.state,
            'zipCode': $scope.patient.zipCode,
            'email': $scope.patient.email
          };
          Patient.createPatient(data).then(function (response) {
		            $scope.patientStatus.isMessage = true;
		            $scope.message = "Patient created successfully"+" with ID "+response.data.patient.id;
          }).catch(function (response) {
		            $scope.patientStatus.isMessage = true;
		            if(response.data.message !== undefined){
		              $scope.patientStatus.message = response.data.message;
		            }else{
		              $scope.patientStatus.message = 'Error occured! Please try again';
		           }
          });
        };


        $scope.deletePatient = function(){
        	$scope.patient.id="1"
          	Patient.deletePatient($scope.patient.id).then(function (response) {
		            $scope.isMessage = true;  
		            $scope.patientStatus.message = response.data.message;
          }).catch(function (response) {
		            $scope.isMessage = true;  
		            if(response.data.message != undefined){
		              	$scope.message = response.data.message;
		            }else{
		              	$scope.patientStatus.message = 'Error occured! Please try again';
		           }
          });
        };

        //
         angular.element("#dp2").datepicker().datepicker("setDate", new Date());


        angular.element('#dp2').on('changeDate', function(ev){

        var currentDate = new Date();
        var selectedDate = new Date($(this).val());
        var age = currentDate.getFullYear() - selectedDate.getFullYear();
        var m = currentDate.getMonth() - selectedDate.getMonth();
        if (m < 0 || (m === 0 && currentDate.getDate() < selectedDate.getDate())) {
            age--;
        }
        angular.element('.age').val(age);
        $scope.patient.age=age;

        angular.element("#dp2").datepicker('hide');
      });

        //
      }
    };
  });