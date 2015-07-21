'use strict';

angular.module('hillromvestApp')
    .controller('ResetFinishController', function ($scope, $stateParams, $timeout, Auth, localStorageService, $state) {

        $scope.keyMissing = $stateParams.key === undefined;
        $scope.doNotMatch = null;
        $scope.showCaptcha = false;
        $scope.resetAccount = {};
        $scope.questions = [];
        $scope.questionsNotLoaded = false;
        $scope.questionVerificationFailed =false;
        $scope.submitted  = false;
        $scope.otherError = false;
        $timeout(function (){angular.element('[ng-model="resetAccount.password"]').focus();});
        $scope.formSubmit = function(){
             $scope.submitted  = true;
        }
        Auth.getSecurityQuestions().
        then(function (response) {
            $scope.questions = response.data;
        }).catch(function (err) {
            $scope.questionsNotLoaded = true;  
        });

        $scope.finishReset = function() {
            if($scope.form.$invalid){
                return false;
            }
        	event.preventDefault();
        	 $scope.error = null;
            if ($scope.resetAccount.password !== $scope.resetAccount.confirmPassword) {
                $scope.doNotMatch = 'ERROR';
            } else {
            	$scope.doNotMatch = null;
                Auth.resetPasswordFinish($stateParams.key,$scope.resetAccount).then(function () {
                    $scope.success = 'OK';
                    localStorage.setItem('resetFinishCount',0);
                    $state.go('home');
                }).catch(function (response) {
                    $scope.success = null;
                    
                    if(response.status === 400 && response.data.ERROR === 'Invalid Reset Key'){
                    	$scope.error = true;
                    }else if(response.status === 400 && response.data.ERROR === 'Incorrect Security Question or Password'){
                        $scope.questionVerificationFailed = true;
                    }else{
                       $scope.otherError = true; 
                    }
                });
            }
        };
    });
