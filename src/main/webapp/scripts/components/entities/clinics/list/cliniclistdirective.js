'use strict';

angular.module('hillromvestApp')
.directive('clinicList', function(ClinicService) {
  return {
    templateUrl: 'scripts/components/entities/clinics/list/list.html',
    restrict: 'E',
    scope: {
      onSelect: '&',
      onCreate: '&',
      clinicStatus: '=clinicStatus'
    },
    link: function(scope) {
      scope.$on('resetList', function () {
        scope.init();
      });
    },

    controller: function($scope, $timeout) {

      $scope.init = function () {
        $scope.currentPageIndex = 1;
        $scope.perPageCount = 10;
        $scope.pageCount = 0;
        $scope.total = 0;
        $scope.clinics = [];
        $scope.sortOption ="";
        $scope.showModal = false;
      };


      var timer = false;
      $scope.$watch('searchItem', function () {
        if(timer){
          $timeout.cancel(timer)
        }
        timer= $timeout(function () {
            $scope.searchClinics();
        },1000)
      });

      $scope.searchClinics = function (track) {
        if (track !== undefined) {
          if (track === "PREV" && $scope.currentPageIndex > 1) {
            $scope.currentPageIndex--;
          }
          else if (track === "NEXT" && $scope.currentPageIndex < $scope.pageCount)
            {
              $scope.currentPageIndex++;
            }else{
              return false;
            }
        }
        ClinicService.getClinics($scope.searchItem, $scope.sortOption, $scope.currentPageIndex, $scope.perPageCount).then(function (response) {
          $scope.clinics = response.data;
          $scope.total = response.headers()['x-total-count'];
          $scope.pageCount = Math.ceil($scope.total / 10);
        }).catch(function (response) {

        });
      };

      $scope.selectClinic = function(clinic) {
        $scope.clinic = clinic;
        ClinicService.getClinic(clinic.id).then(function (response) {
          $scope.clinic = response.data;
          if (clinic.parentClinic) {
            $scope.clinic.type = 'child';
          } else {
            $scope.clinic.type = 'parent';
          }
          $scope.onSelect({
            'clinic': $scope.clinic
          });
        }).catch(function (response) {

        });
      };

      $scope.createClinic = function(){
          $scope.onCreate();
      };

      $scope.init();
    }
  }
});
