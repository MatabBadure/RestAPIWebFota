'use strict';

angular.module('hillromvestApp')
  .directive('patientList', function(UserService, patientService, $state) {
    return {
      templateUrl: 'scripts/app/modules/admin/patient/directives/list/patientlist.html',
      restrict: 'E',
      scope: {
        onSelect: '&',
        onCreate: '&',
        patientStatus: '=patientStatus'
      },
      link: function(scope, element, attrs) {
        var patient = scope.patient;
        scope.$on('resetList', function () {
          scope.searchPatients();
        })
      },
      controller: function($scope, $timeout, dateService) {

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
          $state.go('patientEdit', {
            'patientId': patient.id
          });
        };

        $scope.createPatient = function() {
          $state.go('patientNew');
        };

        $scope.searchPatients = function(track) {
          if (track !== undefined) {
            if (track === "PREV" && $scope.currentPageIndex > 1) {
              $scope.currentPageIndex--;
            } else if (track === "NEXT" && $scope.currentPageIndex < $scope.pageCount) {
              $scope.currentPageIndex++;
            } else {
              return false;
            }
          } else {
            $scope.currentPageIndex = 1;
          }
          var url = 'api/user/patient/search?searchString=';
          UserService.getUsers(url,$scope.searchItem, $scope.sortOption, $scope.currentPageIndex, $scope.perPageCount)
          .then(function(response) {
            $scope.patients = response.data;
            var patientCount = $scope.patients.length;
            for (var i = 0 ; i < patientCount ; i++) {
              var _date = dateService.getDate($scope.patients[i].dob),
               _month = dateService.getMonth(_date.getMonth()),
               _day = dateService.getDay(_date.getDate()),
               _year = dateService.getYear(_date.getFullYear()),
               dob = _month+"/"+_day+"/"+_year;
              $scope.patients[i].dob = dob;
            }
            $scope.total = response.headers()['x-total-count'];
            $scope.pageCount = Math.ceil($scope.total / 10);
          }).catch(function(response) {
            $scope.noMatchFound = true;
          });
        };
        $scope.init();
      }
    };
  });