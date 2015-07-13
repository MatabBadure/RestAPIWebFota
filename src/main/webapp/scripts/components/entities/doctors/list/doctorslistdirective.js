'use strict';

angular.module('hillromvestApp')
.directive('doctorList', function () {
  return {
    templateUrl: 'scripts/components/entities/doctors/list/list.html',
    restrict: 'E',
    link: function postLink(scope, element, attrs) {
    },
    scope: {
      onSelect: '&'
    },
    link: function(scope, element, attrs) {
      var doctor = scope.doctor;
    },
    controller: function($scope) {
      $scope.doctors =[];

      $scope.selectDoctor = function(doctor) {
        $scope.doctor = doctor;
        $scope.onSelect({'doctor': doctor});
      },

      $scope.searchDoctors = function(){
        $scope.doctors = [{'firstName':'Johny','lastName':'Dep','name':'Johny Dep','email':'JohnyDep@gmail.com','hospital':'Appolo hospital'}
        ,{'name':'James williams','email':'JamesWilliams@gmail.com','hospital':'Manipal hospital'}
        ,{'name':'David Jones','email':'davijones@gmail.com','hospital':'abc hospital'}
        ,{'name':'William Davis','email':'williamdavis@gmail.com','hospital':'mno hospital'}
        ,{'name':'Joseph taylor','email':'josephtaylor@gmail.com','hospital':'xyz hospital'}
        ,{'name':'David Jones','email':'davijones@gmail.com','hospital':'abc hospital'}];
      }
      
      
    }
    
    //
  };
});