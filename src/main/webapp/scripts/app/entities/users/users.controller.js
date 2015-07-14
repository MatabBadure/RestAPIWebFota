'use strict';

angular.module('hillromvestApp')
  .controller('UsersController', function ($scope) {
    $scope.user = {};
    $scope.isCreate =true;
    $scope.isMessage =false;
    /*$scope.userStatus ={
		'isCreate':true,
		'isMessage':false
	}; */
    $scope.selectedUser = function (user) {
    	
     /*$scope.userStatus = {
		'isCreate':false,
		'isMessage':false
	};*/
    	$scope.isCreate =false;
    	$scope.isMessage =false;
        $scope.user = user;
    };
  });

