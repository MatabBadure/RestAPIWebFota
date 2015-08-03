'use strict';

angular.module('hillromvestApp')
  .directive('doctorList', function(UserService) {
    return {
      templateUrl: 'scripts/components/entities/doctors/list/list.html',
      restrict: 'E',
      scope: {
        onSelect: '&',
        onCreate: '&',
        doctorStatus: '=doctorStatus'
      },
      link: function(scope, element, attrs) {
        var doctor = scope.doctor;
        scope.$on('resetList', function() {
          scope.searchDoctors();
        })
      },

      controller: function($scope, $timeout) {

        $scope.init = function() {
          $scope.doctors = [];
          $scope.doctorInfo = {};
          $scope.currentPageIndex = 1;
          $scope.perPageCount = 10;
          $scope.pageCount = 0;
          $scope.total = 0;
          $scope.noMatchFound = false;
          $scope.sortOption = "";
          $scope.showModal = false;
        };


        var timer = false;
        $scope.$watch('searchItem', function() {
          if (timer) {
            $timeout.cancel(timer)
          }
          timer = $timeout(function() {
            $scope.searchDoctors();
          }, 1000)
        });


        $scope.selectDoctor = function(doctor) {
          $scope.doctor = doctor;
          var url = '/api/user/' + doctor.id + '/hcp';
          UserService.getUser(doctor.id, url).then(function(response) {
            $scope.doctor = response.data.user;
            //Todo : Clinics should come in user from backend
            $scope.doctor.clinics = response.data.clinics;
            $scope.onSelect({
              'doctor': $scope.doctor
            });
          }).catch(function(response) {});
        };

        $scope.createDoctor = function() {
          $scope.onCreate();
        };

        $scope.searchDoctors = function(track) {
          if (track !== undefined) {
            if (track === "PREV" && $scope.currentPageIndex > 1) {
              $scope.currentPageIndex--;
            } else if (track === "NEXT" && $scope.currentPageIndex < $scope.pageCount) {
              $scope.currentPageIndex++;
            } else {
              return false;
            }
          }else {
            $scope.currentPageIndex = 1;
          }
          var url = 'api/user/hcp/search?searchString=';
          UserService.getUsers(url, $scope.searchItem, $scope.sortOption, $scope.currentPageIndex, $scope.perPageCount).then(function(response) {
            $scope.doctors = response.data;
            $scope.total = response.headers()['x-total-count'];
            $scope.pageCount = Math.ceil($scope.total / 10);
            if ($scope.total == 0) {
              $scope.noMatchFound = true;
            } else {
              $scope.noMatchFound = false;
            }
          }).catch(function(response) {

          });
        };

        $scope.init();
      }
    };
  });
