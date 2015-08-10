'use strict';

angular.module('hillromvestApp')
.directive('clinicList', function() {
  return {
    templateUrl: 'scripts/app/modules/admin/clinic/directives/list/list.html',
    restrict: 'E',
    scope: {
      onSelect: '&',
      onCreate: '&',
      clinicStatus: '=clinicStatus'
    },
    link: function(scope) {
      scope.$on('resetList', function () {
        scope.searchClinics();
      });
    },

    controller: function($scope, $timeout, $state, clinicService) {

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
          else if (track === "NEXT" && $scope.currentPageIndex < $scope.pageCount){
              $scope.currentPageIndex++;
          }
          else{
              return false;
          }
        }else {
            $scope.currentPageIndex = 1;
        }
        clinicService.getClinics($scope.searchItem, $scope.sortOption, $scope.currentPageIndex, $scope.perPageCount).then(function (response) {
          $scope.clinics = response.data;
          $scope.total = response.headers()['x-total-count'];
          $scope.pageCount = Math.ceil($scope.total / 10);
        }).catch(function (response) {

        });
      };

      $scope.selectClinic = function(clinic) {
         $state.go('clinicEdit', {
          'clinicId': clinic.id
        });
      };

      $scope.createClinic = function(){
          $state.go('clinicNew');
      };

      $scope.init();
    }
  }
});
