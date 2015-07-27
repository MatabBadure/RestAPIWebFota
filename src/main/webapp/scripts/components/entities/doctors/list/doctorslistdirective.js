'use strict';

angular.module('hillromvestApp')
.directive('doctorList', function (UserService) {
  return {
    templateUrl: 'scripts/components/entities/doctors/list/list.html',
    restrict: 'E',
    scope: {
      onSelect: '&',
      onCreate: '&'
    },
    link: function(scope, element, attrs) {
      var doctor = scope.doctor;
    },

    controller: function($scope, $timeout) {

      $scope.init = function () {
        $scope.doctors =[];
        $scope.doctorInfo ={};
        $scope.currentPageIndex = 1;
        $scope.perPageCount = 10;
        $scope.pageCount = 0;
        $scope.total = 0;
        $scope.noMatchFound = false;
      };

      $scope.init();

      var timer = false;
      $scope.$watch('searchItem', function () {
        if (timer) {
          $timeout.cancel(timer)
        }
        timer = $timeout(function () {
          if ($scope.searchItem) {
            var url = 'api/user/hcp/search?searchString=';
            UserService.getUsers(url, $scope.searchItem, 1, 10).then(function(response) {
              $scope.doctors = response.data;
            }).catch(function(response) {

            });
          } else {
            $scope.doctors = [];
          }
        },1000)
      });


      $scope.selectDoctor = function(doctor) {

        $scope.doctor = doctor;
        $scope.onSelect({'doctor': doctor});
        //Todo:
        // UserService.getUser(doctor.id).then(function (response) {
        //   response.data.clinicList = [ { 'id': 1, 'name': 'Name1'}, { 'id': 2, 'name': 'Name2'}];
        //   console.log('Complete Object: ', $scope.doctor);
        //   $scope.doctor = response.data;
        //   $scope.onSelect({'doctor': doctor});
        // }).catch(function (response) {

        // });
      };

       $scope.createDoctor = function(){
          $scope.onCreate();
        };

      $scope.searchDoctors = function(track) {
        if (track !== undefined) {
          if (track === "PREV" && $scope.currentPageIndex > 1) {
            $scope.currentPageIndex--;
          }
          if (track === "NEXT") {
            $scope.currentPageIndex++;
          }
        }
        var url = 'api/user/hcp/search?searchString=';
        UserService.getUsers(url, $scope.searchItem, $scope.currentPageIndex, $scope.perPageCount).then(function (response) {
          $scope.doctors = response.data;
          $scope.total = response.headers()['x-total-count'];
          $scope.pageCount = Math.floor($scope.total / 10)+1;
          if($scope.total == 0){
            $scope.noMatchFound = true;
          }else{
            $scope.noMatchFound = false;
          }
        }).catch(function(response) {

        });
      };
    }
  };
});