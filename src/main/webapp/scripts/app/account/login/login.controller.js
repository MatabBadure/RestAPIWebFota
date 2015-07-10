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
            Auth.login({
                username: $scope.username,
                password: $scope.password,
                captcha: $scope.user.captcha
            }).then(function (data) {
              $scope.authenticationError = false;
              if(data.status === 200){
                localStorage.removeItem('loginCount');
                $state.go('patient');
                localStorage.setItem('token', data.data.token);
              }
            }).catch(function (data) {
              if (data.status === 401) {
                if(!data.data.APP_CODE){
                  $scope.authenticationError = true;
                  var loginCount = parseInt(localStorage.getItem('loginCount')) || 0;
                  localStorage.setItem('loginCount', loginCount + 1);
                  if (loginCount > 2) {
                    $scope.showCpatcha = true;
                  }
                }else if (data.data.APP_CODE === 'EMAIL_PASSWORD_RESET') {
                  localStorage.setItem('token', data.data.token);
                  $scope.isFirstLogin = true;
                  $scope.isEmailExist = false;
                  $scope.showLogin = false;
                } else if (data.data.APP_CODE === 'PASSWORD_RESET') {
                  localStorage.setItem('token', data.data.token);
                  $scope.isFirstLogin = true;
                  $scope.showLogin = false;
                }
              }

                // var loginCount = parseInt(localStorage.getItem('loginCount')) || 0;
                // localStorage.setItem('loginCount', loginCount + 1);
                // if(loginCount > 2){
                //   $scope.showCpatcha = true;
                // }
            });
        };

        $scope.submitPassword = function (event){
          event.preventDefault();
          Auth.submitPassword({
                email: $scope.user.email,
                password: $scope.user.password
            }).then(function (data) {
              Auth.logout();
              $state.go('home');
            }).catch(function (err) {
              Auth.logout();
              console.log('Error...!');
            });
        };
    });