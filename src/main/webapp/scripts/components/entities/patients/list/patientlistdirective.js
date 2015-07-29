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
        };

        $scope.init();

        var timer = false;
        $scope.$watch('searchItem', function() {
          if (timer) {
            $timeout.cancel(timer)
          }
          timer = $timeout(function() {
            if ($scope.searchItem) {
              PatientService.getPatientList($scope.searchItem, 1, 10).then(function(response) {
                $scope.patients = response.data;
              }).catch(function(response) {

              });
            } else {
              $scope.patients = [];
            }
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
            if (track === "NEXT")
              $scope.currentPageIndex++;
          } else {
            $scope.currentPageIndex = 1;
          }
          PatientService.getPatientList($scope.searchItem, $scope.currentPageIndex, $scope.perPageCount)
            .then(function(response) {
              $scope.patients = response.data;
              $scope.total = response.headers()['x-total-count'];
              $scope.pageCount = Math.ceil($scope.total / 10);
            }).catch(function(response) {
              $scope.noMatchFound = true;
            });
        }
      }
    };
  });