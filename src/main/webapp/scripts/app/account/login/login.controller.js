'use strict';

angular.module('hillromvestApp')
.controller('LoginController', function($scope, $state, $timeout, Auth, vcRecaptchaService, globalConfig) {
  $scope.showLogin = true;
  $scope.isEmailExist = true;
  $scope.isFirstLogin = false;
  $scope.showCaptcha = false;
  $scope.response = null;
  $scope.widgetId = null;
  $scope.user = {};
  $scope.errors = {};
  $scope.questions = [];
  $scope.authenticationError = false;
  $scope.siteKey = globalConfig.siteKey;
  $scope.loginSubmitted = false;
  $scope.submitted = false;
  $scope.firstTimeAccessFailed = false;
  $scope.otherError = false;

  $scope.setResponse = function(response) {
    $scope.response = response;
  };

  $scope.setWidgetId = function(widgetId) {
    $scope.widgetId = widgetId;
  };

  $scope.submitConfirmForm = function() {
    $scope.submitted = true;
  };

  Auth.getSecurityQuestions().
  then(function(response) {
    $scope.questions = response.data;
  }).catch(function(err) {
    $scope.questionsNotLoaded = true;
  });

  $scope.authenticate = function() {
    $scope.authenticationError = false;
    Auth.login({
      username: $scope.username,
      password: $scope.password,
      captcha: $scope.user.captcha
    }).then(function(data) {
      if (data.status === 200) {
        localStorage.removeItem('loginCount');
        $state.go('patient');
      }
    }).catch(function(data) {
      if (data.status === 401) {
        if (!data.data.APP_CODE) {
          $scope.authenticationError = true;
          var loginCount = parseInt(localStorage.getItem('loginCount')) || 0;
          localStorage.setItem('loginCount', loginCount + 1);
          if (loginCount > 2) {
            $scope.showCaptcha = true;
          }
        } else if (data.data.APP_CODE === 'EMAIL_PASSWORD_RESET') {
          localStorage.setItem('token', data.data.token);
          $scope.isFirstLogin = true;
          $scope.isEmailExist = false;
          $scope.showLogin = false;
        } else if (data.data.APP_CODE === 'PASSWORD_RESET') {
          localStorage.setItem('token', data.data.token);
          $scope.isFirstLogin = true;
          $scope.showLogin = false;
        }else{
          $scope.otherError = true;
        }
      }
      else{
        $scope.otherError = true;
      }
    });
  };

  $scope.submitPassword = function(event) {
    if ($scope.confirmForm.$invalid) {
      return false;
    }
    event.preventDefault();
  //
  if ($scope.user.password !== $scope.user.confirmPassword) {
    $scope.doNotMatch = true;
  } else {
    $scope.doNotMatch = false;
    Auth.submitPassword({
      'email': $scope.user.email,
      'password': $scope.user.password,
      'answer': $scope.user.answer,
      'questionId': $scope.user.question.id,
      'termsAndConditionsAccepted': $scope.user.tnc
    }).then(function(data) {
      Auth.logout();
      $state.go('home');
    }).catch(function(err) {
      Auth.logout();
      $scope.firstTimeAccessFailed = true;
    });
  }
};

$timeout(function() {
  angular.element('[ng-model="username"]').focus();
});

$scope.login = function(event) {
  $scope.loginSubmitted = true;
  if ($scope.form.username.$invalid || $scope.form.username.$invalid || ($scope.showCaptcha && $scope.response === null)) {
    return false;
  }
  event.preventDefault();
  if ($scope.showCaptcha) {
    Auth.captcha($scope.response).then(function(data) {
      $scope.showCaptcha = false;
      $scope.captchaError = false;
      $scope.authenticate();
    }).catch(function(err) {
      $scope.captchaError = true;
      $scope.response = null;
      vcRecaptchaService.reload($scope.widgetId);
    });
  } else {
    $scope.authenticate();
  }
};
});
