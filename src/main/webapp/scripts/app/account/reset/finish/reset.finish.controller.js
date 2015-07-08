'use strict';

angular.module('hillromvestApp')
    .controller('ResetFinishController', function ($scope, $stateParams, $timeout, Auth, localStorageService, $state) {

        $scope.keyMissing = $stateParams.key === undefined;
        $scope.doNotMatch = null;
        $scope.showCaptcha = false;
        $scope.resetAccount = {};
        $timeout(function (){angular.element('[ng-model="resetAccount.password"]').focus();});

        $scope.finishReset = function() {
        	event.preventDefault();
        	 $scope.error = null;
            if ($scope.resetAccount.password !== $scope.confirmPassword) {
                $scope.doNotMatch = 'ERROR';
            } else {
            	$scope.doNotMatch = null;
                Auth.resetPasswordFinish($stateParams.key,$scope.resetAccount.password).then(function () {
                    $scope.success = 'OK';
                    localStorage.setItem('resetFinishCount',0);
                    $state.go('home');
                }).catch(function (response) {
                    $scope.success = null;
                    if(response.status === 400){
                    	$scope.error = 'ERROR';
                    }
                });
            }
        };
    });
