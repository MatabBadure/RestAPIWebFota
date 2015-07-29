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

        $scope.authenticate = function(event) {
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

        $scope.passwordStrength = function(){
            console.info('passwordStrength Called...!');
            $scope.display_strength('passwordBox','passwordStrengthContainer','status');
        };


        $scope.div = function (x){
            return document.getElementById(x);
        };

        $scope.display_strength = function (x,y,z) {
            console.log($scope.div(x));
            if(!x || $scope.div(x) === "")
            {
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

            var count = [42,52,63,74];
            var color = ["red","gold","lime","darkgreen"];
            var value = ["weak","Good","Strong","Very Strong"];
            var index = 0;

            for(index=0; index<regex.length; index++){
                if(index > regex.length){
                    break;
                }
                if(regex[index].test(paswd.value)){
                    break;
                }
                else{
                    continue;
                }

            }

            years = $scope.compute_strength(count[index],(paswd.value).length);

            if(years>= 1000){
                stren.style.width = "100%";
                stren.style.background = color[3];
                stats.innerHTML = value[3];
                stats.style.color =  color[3];
            }else if(years>=100 && years<1000){
                stren.style.width = "75%";
                stren.style.background = color[2];
                stats.innerHTML = value[2];
                stats.style.color = color[2];
            }
            else if(years>=10 && years<100){
                stren.style.width = "50%";
                stren.style.background = color[1];
                stats.innerHTML = value[1];
                 stats.style.color = color[1];
            }else{
                stren.style.width = "25%";
                stren.style.background = color[0];
                stats.innerHTML = value[0];
                stats.style.color = color[0];
            }
            return false;
        };

        $scope.compute_strength = function (x, y){
            if(!x){
                return -1;
            }
            var secondsInAYear = 31557600;
            var permutateRepeat = (Math.pow(x,y) - (x*$scope.factorial(y) - 1));
            var countManyYears = Math.floor(permutateRepeat/(1000000*secondsInAYear));

            return countManyYears;
        };

         $scope.factorial = function (x) {
            if(x<0){
                return false;
            }

            if(x == 0 ){
                return 1;
            }

            x = Math.floor(x);
            return (x*$scope.factorial(x-1));

        };

});