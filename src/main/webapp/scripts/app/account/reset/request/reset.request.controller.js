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

        
       /* $http.get("scripts/app/account/reset/request/text.json").success(function (data) {
            console.log('success: ' + data)
        });
        */
        
        $scope.requestReset = function () {
        	console.log($scope.form.valid);
        	event.preventDefault();
        	var data = {"email": $scope.resetAccount.email};
        	$http.post('api/account/reset_password/init', data, {
                headers: {
                    "Content-Type": "application/json",
                    "Accept": "application/json"
                }
            }).success(function (response) {
                console.log('Success');
            }).error(function (response) {
            	console.log('Error', response);
            });
        	/*
        	 var request = $http({
                 method: "post",
                 url: "api/account/reset_password/init",
                 data: data,
                 contentType: 'application/json'
             });
        	 
        	 request.success(function(data){
        		 console.log("success");
        	 }).error(function(data){
        		 console.log("fail");
        	 })
        	 */
        	
        	  /*if ($scope.showCaptcha) {
        		  //
        		  var captchaData = $scope.user.captcha.response;
        		  $.ajax({
        		        type: "POST",
        		        url: "https://www.google.com/recaptcha/api/siteverify",
        		        dataType: 'json',
        		        crossDomain: true,
        		        data:  {
        	                    'secret': '6LfwMAkTAAAAAHnNpBlH7fEixBPQBqLffYfArQ0E',
        	                    'response': captchaData,
        	                    'remoteip' :'10.132.161.102'
        	                },
        		        success: function (jsonResult) {
        		           //do what ever with the reply
        		        	console.log(jsonResult);
        		        },
        		        error: function (jqXHR, textStatus) {
        		            //handle error
        		        	console.log(jqXHR);
        		        }
        		    });
        		  
        		  
        		  //
               Auth.captcha($scope.user.captcha).then(function (data) {
                 console.log(data)
                 Auth.resetPasswordInit($scope.resetAccount.email).then(function () {
                     $scope.success = 'OK';
                     localStorage.setItem('passResetCount',0);
                 }).catch(function (response) {
                     $scope.success = null;
                     if (response.status === 400 && response.data === 'e-mail address not registered') {
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
               }).catch(function (err) {
                 console.log('ERROR :::',err)
               });
             }else*/
        	
        	
            	/* Auth.resetPasswordInit($scope.resetAccount.email).then(function () {
                     $scope.success = 'OK';
                     localStorage.setItem('passResetCount',0);
                 }).catch(function (response) {
                     $scope.success = null;
                     if (response.status === 400 && response.data === 'e-mail address not registered') {
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
        	*/
        }

    });
