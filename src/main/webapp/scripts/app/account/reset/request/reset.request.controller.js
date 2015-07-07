'use strict';

angular.module('hillromvestApp')
    .controller('RequestResetController', function ($rootScope, $scope, $state, $timeout, Auth, localStorageService, $http) {

        $scope.success = null;
        $scope.error = null;
        $scope.errorEmailNotExists = null;
        $scope.resetAccount = {};
        $scope.showCaptcha = false;
        $scope.user = {};
        $timeout(function (){angular.element('[ng-model="resetAccount.email"]').focus();});

        
        $scope.requestReset = function () {
        	console.log($scope.form.valid);
        	event.preventDefault();
            	 Auth.resetPasswordInit($scope.resetAccount.email).then(function () {
                     $scope.success = 'OK';
                     $scope.errorEmailNotExists = null;
                     localStorage.setItem('passResetCount',0);
                 }).catch(function (response) {
                     $scope.success = null;
                     if (response.status === 400 && response.data.message === 'e-mail address not registered') {
                         $scope.errorEmailNotExists = 'ERROR';
                         var passResetCount = parseInt(localStorage.getItem('passResetCount')) || 0;
                         localStorage.setItem('passResetCount', passResetCount + 1);
                         if(passResetCount > 2){
                         	$scope.showCaptcha = true;	 
                         }
                     } else {
                         $scope.error = 'ERROR';
                     }
                 });
        	
        }

    });
