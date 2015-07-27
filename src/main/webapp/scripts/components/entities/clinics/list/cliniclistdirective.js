'use strict';

angular.module('hillromvestApp')
.directive('clinicList', function(ClinicService) {
  return {
    templateUrl: 'scripts/components/entities/clinics/list/list.html',
    restrict: 'E',
    scope: {
      onSelect: '&',
      onCreate: '&'
    },
    link: function(scope) {
      var clinic = scope.clinic;
    },
    controller: function($scope, $timeout) {

      $scope.init = function () {
        $scope.currentPageIndex = 1;
        $scope.perPageCount = 10;
        $scope.pageCount = 0;
        $scope.total = 0;
        $scope.clinics = [];
      };

      $scope.init();

      var timer = false;
      $scope.$watch('searchItem', function () {
        if(timer){
          $timeout.cancel(timer)
        }
        timer= $timeout(function () {
          if ($scope.searchItem) {
            ClinicService.getClinics($scope.searchItem, 1, 10).then(function (response) {
            $scope.clinics = response.data;
          }).catch(function (response) {

          });
          } else {
            $scope.clinics = [];
          }
        },1000)
      });

      $scope.selectClinic = function (clinic) {
        $scope.clinic = clinic;
        $scope.onSelect({
          'clinic': clinic
        });
      };

      $scope.searchClinics = function (track) {
        if (track !== undefined) {
          if (track === "PREV" && $scope.currentPageIndex > 1) {
            $scope.currentPageIndex--;
          }
          if (track === "NEXT") {
            $scope.currentPageIndex++;
          }
        }
        ClinicService.getClinics($scope.searchItem, $scope.currentPageIndex, $scope.perPageCount).then(function (response) {
          $scope.clinics = response.data;
        }).catch(function (response) {

        });
      };

      $scope.selectClinic = function(clinic) {
        $scope.clinic = clinic;
        if (clinic.parent) {
          $scope.clinic.type = 'parent';
        } else {
          $scope.clinic.type = 'child';
        }
        $scope.onSelect({'clinic': clinic});
      };

      $scope.createClinic = function(){
          $scope.onCreate();
      };
    }
  }
});
