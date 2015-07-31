'use strict';

angular.module('hillromvestApp')
  .directive('patientList', function(UserService, PatientService) {
    return {
      templateUrl: 'scripts/components/entities/patients/list/patientlist.html',
      restrict: 'E',
      scope: {
        onSelect: '&',
        onCreate: '&',
        patientStatus: '=patientStatus'
      },
      link: function(scope, element, attrs) {
        var patient = scope.patient;
      },
      controller: function($scope, $timeout) {

        $scope.init = function() {
          $scope.patients = [];
          $scope.patientInfo = {};
          $scope.currentPageIndex = 1;
          $scope.perPageCount = 10;
          $scope.pageCount = 0;
          $scope.total = 0;
          $scope.noMatchFound = false;
          $scope.sortOption ="";
          $scope.showModal = false;
          $scope.searchPatients();
        };

        var timer = false;
        $scope.$watch('searchItem', function() {
          if (timer) {
            $timeout.cancel(timer)
          }
          timer = $timeout(function() {
              $scope.searchPatients();
          }, 1000);
        });

        $scope.selectPatient = function(patient) {
          PatientService.getPatientInfo(patient.id).then(function(response) {
            $scope.patientInfo = response.data;
            $scope.patient = $scope.patientInfo;
            $scope.onSelect({
              'patient': $scope.patient
            });
          }).catch(function(response) {
            console.log("get Patient Info failed!");
          });
        };

        $scope.createPatient = function() {
          $scope.onCreate();
        };

        $scope.searchPatients = function(track) {
          if (track !== undefined) {
            if (track === "PREV" && $scope.currentPageIndex > 1)
              $scope.currentPageIndex--;
            else if (track === "NEXT" && $scope.currentPageIndex < $scope.pageCount)
            {
              $scope.currentPageIndex++;
            }else{
              return false;
            }
          } else {
            $scope.currentPageIndex = 1;
          }
          var url = 'api/user/patient/search?searchString=';
          UserService.getUsers(url,$scope.searchItem, $scope.sortOption, $scope.currentPageIndex, $scope.perPageCount)
            .then(function(response) {
              $scope.patients = response.data;
              $scope.total = response.headers()['x-total-count'];
              $scope.pageCount = Math.ceil($scope.total / 10);
            }).catch(function(response) {
              $scope.noMatchFound = true;
            });
        }
        $scope.init();
      }
    };
  });