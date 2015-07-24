'use strict';

angular.module('hillromvestApp')
    .controller('AuthenticateController', function ($scope, Auth, localStorageService, $state, $stateParams) {

        $scope.otherError = false;
        $scope.questionsNotLoaded = false;
        $scope.questionVerificationFailed = false;
        $scope.success = false;
        $scope.doNotMatch = false;
        $scope.alreadyActive = false;
        $scope.authenticate = {};
        
        $scope.formSubmit = function(){
             $scope.submitted  = true;
        }
        Auth.getSecurityQuestions().
        then(function (response) {
            $scope.questions = response.data;
        }).catch(function (err) {
            $scope.questionsNotLoaded = true;
        });

        $scope.authenticate = function() {
        	event.preventDefault();
        	 $scope.error = null;
            if ($scope.authenticate.password !== $scope.authenticate.confirmPassword) {
                $scope.doNotMatch = 'ERROR';
            } else {
            	$scope.doNotMatch = null;
            	var data = {
					        "questionId" : $scope.authenticate.question.id,
					        "answer" : $scope.authenticate.answer,
					        "password" : $scope.authenticate.password,
					        "termsAndConditionsAccepted" : $scope.authenticate.tnc,
					        "key" : $stateParams.key
					      }
                Auth.configurePassword(data).then(function () {
                    $scope.success = true;
                    $state.go('home');
                }).catch(function (response) {
                    $scope.success = false;
                    if(response.status == 400 && response.data.ERROR == "Invalid Activation Key"){
                    	$scope.alreadyActive = true;
                    }else{
                    	$scope.otherError = true;
                    }
                });
            }
        };
    });
