'use strict';

angular.module('hillromvestApp')
    .controller('LoginController', function ($rootScope, $scope, $state, $timeout, Auth) {
        $scope.showLogin = true;
        $scope.isEmailExist = true;
        $scope.isFirstLogin = false;
        $scope.showCpatcha = false;
        $scope.user = {};
        $scope.errors = {};

        $scope.rememberMe = true;
        $timeout(function (){angular.element('[ng-model="username"]').focus();});
        $scope.login = function (event) {
            event.preventDefault();
            // if ($scope.showCpatcha) {
            //   Auth.captcha($scope.user.captcha).then(function (data) {
            //     console.log(data)
            //   }).catch(function (err) {
            //     console.log('ERROR :::',err)
            //   });
            // }
            Auth.login({
                username: $scope.username,
                password: $scope.password,
                rememberMe: $scope.rememberMe,
                captcha: $scope.user.captcha
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
                  'isEmailExist': true,
                  'message': 'Some Success Message(If Required to show)',
                  'isFirstLogin': true,
                  'user':{
                    'role': 'admin',
                    'firstName': 'firstName',
                    'lastName': 'LastName'
                  },
                  'token': 'someToken'
                };
                if (data.status === 200 && data.isFirstLogin === true) {
                  $scope.isEmailExist = data.isEmailExist;
                  $scope.isFirstLogin = data.isFirstLogin;
                  $scope.showLogin = false;
                }else{
                  console.log('Redirecting to Home Page...!');
                  $state.go('home');
                }
            }).catch(function () {
                $scope.authenticationError = true;
                var loginCount = parseInt(localStorage.getItem('loginCount')) || 0;
                localStorage.setItem('loginCount', loginCount + 1);
                if(loginCount > 2){
                  $scope.showCpatcha = true;
                }
            });
        };

        $scope.submitPassword = function (event){
          event.preventDefault();
          /*
          Auth.submitPassword({
                email: $scope.user.email,
                password: $scope.user.password,
                confirmPassword: $scope.user.confirmPassword
            }).then(function (data) {
              $state.go('home');
            }).catch(function (err) {

            });
          */
          $state.go('home');
        };

    });
