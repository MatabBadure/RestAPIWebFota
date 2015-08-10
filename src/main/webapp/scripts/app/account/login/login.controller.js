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
    $scope.message = "";

    $scope.setResponse = function(response) {
      $scope.response = response;
    };

    $scope.setWidgetId = function(widgetId) {
      $scope.widgetId = widgetId;
    };

    $scope.submitConfirmForm = function() {
      $scope.submitted = true;
    };

    $scope.init = function() {
      //Todo : needs to move into Utility Service
      localStorage.removeItem('token');
    };

    Auth.getSecurityQuestions().then(function(response) {
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
          localStorage.setItem('userFirstName', data.data.user.firstName);
          if(data.data.user.authorities[0].name === 'PATIENT'){
            $state.go('patientclinic');
          } else {
            $state.go('patientUser');
          }
          
        }
      }).catch(function(data) {
        if (data.status === 401) {
          if (!data.data.APP_CODE) {
            $scope.message = data.data.Error;
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
          } else {
            $scope.otherError = true;
          }
        } else {
          $scope.authenticationError = true;
          $scope.message = "Authentication failed! Please check your credentials and try again."
        }
      });
    };

    $scope.submitPassword = function(event) {
      if ($scope.confirmForm.$invalid) {
        return false;
      }
      event.preventDefault();

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
          if(err.data && err.data.ERROR){
            $scope.message = err.data.ERROR;
          }
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

    $scope.passwordStrength = function() {
      $scope.display_strength('passwordBox', 'passwordStrengthContainer', 'status');
    };

    $scope.div = function(x) {
      return document.getElementById(x);
    };

    $scope.compute_strength = function(x, y) {
      if (!x) {
        return -1;
      }
      var secondsInAYear = 31557600;
      var permutateRepeat = (Math.pow(x, y) - (x * $scope.factorial(y) - 1));
      var countManyYears = Math.floor(permutateRepeat / (1000000 * secondsInAYear));

      return countManyYears;
    };

    $scope.factorial = function(x) {
      if (x < 0) {
        return false;
      }

      if (x == 0) {
        return 1;
      }

      x = Math.floor(x);
      return (x * $scope.factorial(x - 1));

    };

    $scope.display_strength = function(x, y, z) {
      if (!x || $scope.div(x) === "") {
        $scope.div(y).style.width = 0 + "%";
        $scope.div(z).innerHTML = "&nbsp:";
        return false;
      }
      var paswd = $scope.div(x);
      var stren = $scope.div(y);
      var stats = $scope.div(z);
      var years = 0;

      var regex = [
        /^[a-zA-Z]+$/g,
        /^[a-zA-Z0-9]+$/g,
        /^[a-zA-Z0-9\ \~\!\@\#\$\%\^\&\*\(\)]+$/g,
      ];

      var count = [42, 52, 63, 74];
      var color = ["red", "gold", "lime", "darkgreen"];
      var value = ["weak", "Good", "Strong", "Very Strong"];
      var index = 0;

      for (index = 0; index < regex.length; index++) {
        if (index > regex.length) {
          break;
        }
        if (regex[index].test(paswd.value)) {
          break;
        } else {
          continue;
        }

      }

      years = $scope.compute_strength(count[index], (paswd.value).length);

      if (years >= 1000) {
        stren.style.width = "100%";
        stren.style.background = color[3];
        stats.innerHTML = value[3];
        stats.style.color = color[3];
      } else if (years >= 100 && years < 1000) {
        stren.style.width = "75%";
        stren.style.background = color[2];
        stats.innerHTML = value[2];
        stats.style.color = color[2];
      } else if (years >= 10 && years < 100) {
        stren.style.width = "50%";
        stren.style.background = color[1];
        stats.innerHTML = value[1];
        stats.style.color = color[1];
      } else {
        stren.style.width = "25%";
        stren.style.background = color[0];
        stats.innerHTML = value[0];
        stats.style.color = color[0];
      }
      return false;
    };
    $scope.init();
  });
