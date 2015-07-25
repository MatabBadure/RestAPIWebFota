'use strict';

angular.module('hillromvestApp')
.directive('doctorList', function (UserService,DoctorService) {
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
    controller: function($scope) {

      $scope.init = function(){
          $scope.doctors =[];
          $scope.doctorInfo ={};
          $scope.currentPageIndex = 1;
          $scope.perPageCount = 10;
          $scope.pageCount = 0;
          $scope.total = 0;
          $scope.noMatchFound = false;
        };

        $scope.init();

      $scope.selectDoctor = function(doctor) {
        
        UserService.getUser(doctor.id).then(function (response) {
            $scope.doctorInfo = response.data;  
            $scope.doctor = $scope.doctorInfo;
            $scope.onSelect({'doctor': $scope.doctor});
            }).catch(function (response) {
              
           });
      };

       $scope.createDoctor = function(){
          $scope.onCreate();
        };

      $scope.searchDoctors = function(track){
          if(track!==undefined){
            if(track === "PREV" && $scope.currentPageIndex >1)
              $scope.currentPageIndex--;
            if(track === "NEXT")
              $scope.currentPageIndex++;
          }
          DoctorService.getDoctorsList($scope.searchItem,$scope.currentPageIndex,$scope.perPageCount)
          .then(function (response) {
              $scope.doctors = response.data;
              $scope.total = response.headers()['x-total-count'];
              $scope.pageCount = Math.floor($scope.total / 10)+1;
              if($scope.total == 0){
                $scope.noMatchFound = true;
              }else{
                $scope.noMatchFound = false;
              }
              }).catch(function (response) {
              $scope.noMatchFound = true;
         });
        
        } 
    }
  };
});