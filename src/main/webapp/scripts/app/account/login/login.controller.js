'use strict';

angular.module('hillromvestApp')
    .controller('LoginController', function ($rootScope, $scope, $state, $timeout, Auth) {
        $scope.showLogin = true;
        $scope.isEmailExist = true;
        $scope.isFirstLogin = false;
        $scope.user = {};
        $scope.errors = {};

        $scope.rememberMe = true;
        $timeout(function (){angular.element('[ng-model="username"]').focus();});
        $scope.login = function (event) {
            event.preventDefault();
            Auth.login({
                username: $scope.username,
                password: $scope.password,
                rememberMe: $scope.rememberMe
            }).then(function (data) {
                $scope.authenticationError = false;


                // if ($rootScope.previousStateName === 'register') {
                //     $state.go('login');
                // } else {
                //     $rootScope.back();
                // }

                /*Mocking the data For Front-end work*/
                var data = {
                  'status': 200,
                  'isEmailExist': false,
                  'message': 'Some Success Message(If Required to show)',
                  'isFirstLogin': true,
                  'user':{
                    'role': 'admin',
                    'firstName': 'firstName',
                    'lastName': 'LastName'
                  },
                  'token': 'someToken'
                };
                if(data.status === 200 && data.isEmailExist === false){
                  $scope.isEmailExist = data.isEmailExist;
                  $scope.showLogin = false;
                } else if (data.status === 200 && data.isFirstLogin === true) {
                  $scope.isFirstLogin = data.isFirstLogin;
                  $scope.showLogin = false;
                }
            }).catch(function () {
                $scope.authenticationError = true;
                var loginCount = parseInt(localStorage.getItem('loginCount')) || 0;
                localStorage.setItem('loginCount', loginCount + 1);
                if(loginCount > 2){
                  console.log('Enter Captch');
                }
            });
        };

        $scope.submitEmail = function (event) {
          event.preventDefault();
          $scope.isEmailExist = true;
          /*
          Auth.submitEmial({
                email: $scope.user.email,
                password: $scope.user.password,
                confirmPAssword: $scope.user.confirmPassword
            }).then(function (data) {

            }).catch(function (err) {

            });
          */

          $state.go('home');
        };

        $scope.submitPassword = function (event){
          event.preventDefault();
          /*
          Auth.submitEmial({
                password: $scope.user.password,
                confirmPAssword: $scope.user.confirmPassword
            }).then(function (data) {

            }).catch(function (err) {

            });
          */
          $state.go('home');
        };

    });
