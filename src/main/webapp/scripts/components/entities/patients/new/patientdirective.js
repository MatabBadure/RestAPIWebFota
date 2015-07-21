'use strict';

angular.module('hillromvestApp')
  .directive('patient', function (User) {
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
            'hillromId': $scope.patient.HRID,
            'title': $scope.patient.title,
            'gender': $scope.patient.gender,
            'langKey': $scope.patient.language,
            'firstName': $scope.patient.firstName,
            'middleName': $scope.patient.middleName,
            'lastName':$scope.patient.lastName,
            'dob': $scope.patient.dob,
            'age': $scope.patient.age,
            'address': $scope.patient.address,
            'city': $scope.patient.city,
            'state': $scope.patient.state,
            'zipcode': $scope.patient.zipCode,
            'email': $scope.patient.email,
            'role':'PATIENT'
          };
          data = JSON.stringify(data);
          User.createUser(data).then(function (response) {
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


        $scope.deletePatient = function(){
        	$scope.patient.id="1"
          	User.createUser($scope.patient.id).then(function (response) {
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

         angular.element("#dp2").datepicker().datepicker("setDate", new Date());

         angular.element('#dp2').on('changeDate', function(ev){

            var currentDate = new Date();
            var selectedDate = new Date($(this).val());
            $scope.patient.dob = selectedDate.getMonth() +"/"+selectedDate.getDate()+"/"+selectedDate.getFullYear();
            var diff = currentDate - selectedDate ;
            var days = Math.floor(diff/(1000*60*60*24));
            var years = Math.floor(days/365);
            var months = ((days % 365)/30).toFixed(1)
            var age = 0;
            console.log (days + " days" + years + " years" + months + " months")

            if(years === 0){
                if(months > 1){
                  age = months + " months ";
                }else{
                  age = months + " month ";
                }
            }
            if(years > 0){
                  if(years > 1){
                        if(months > 1){
                          age = years + " years " + months + " months ";
                        }else{
                          age = years + " years " + months + " month ";
                        }
                  }else{
                        if(months > 1){
                          age = years + " year " + months + " months ";
                        }else{
                          age = years + " year " + months + " month ";
                        }
                  }
              }

            if(diff < 0){
              age = age-1;
            }
            angular.element('.age').val(age);
            $scope.patient.age=age;
            angular.element("#dp2").datepicker('hide');
        
      });

      }
    };
  });