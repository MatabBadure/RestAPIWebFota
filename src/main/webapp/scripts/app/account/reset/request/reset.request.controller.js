'use strict';

angular.module('hillromvestApp',[])
    .controller('RequestResetController', function ($rootScope, $scope, $state, $timeout, Auth, localStorageService, vcRecaptchaService) {

        $scope.success = null;
        $scope.error = null;
        $scope.errorEmailNotExists = null;
        $scope.resetAccount = {};
        $scope.showCaptcha = true;//change to false
        $scope.user = {};
        $scope.response = null;
        $scope.widgetId = null;
        $scope.siteKey ='6LeJewkTAAAAANM2xOZHtyC3Ehrc6vqZs9Homzvq';
        $timeout(function (){angular.element('[ng-model="resetAccount.email"]').focus();});

        
        $scope.requestReset = function () {
        	event.preventDefault();
        	
        	if($scope.showCaptcha){
        		 $scope.setResponse = function (response) {
                     console.info('Response available', response);
                     $scope.response = response;
                 };
                 
                 $scope.setWidgetId = function (widgetId) {
                     console.info('Created widget ID:', widgetId);

                     $scope.widgetId = widgetId;
                 };
                 
                   Auth.captcha($scope.response).then(function (data) {
                    console.log(data);
                    $scope.showCaptcha = false;
                    //regular reset passowrd flow from here
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
                    //
                   }).catch(function (err) { 
                     console.log('ERROR :::',err);
                     // reloading re-Captcha widget
                     vcRecaptchaService.reload($scope.widgetId);
                     
                   });
        	}else{
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
        }

    });
