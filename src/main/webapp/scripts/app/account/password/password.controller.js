'use strict';

angular.module('hillromvestApp')
    .controller('PasswordController', function ($scope, Auth, Principal) {
        Principal.identity().then(function(account) {
            $scope.account = account;
        });

        $scope.success = null;
        $scope.error = null;
        $scope.doNotMatch = null;
        $scope.submitted = false;
        $scope.formSubmit = function(){
            $scope.submitted = true;
        }
        $scope.changePassword = function () {
            if($scope.form.$invalid){
                return false;
            }
            if ($scope.password !== $scope.confirmPassword) {
                $scope.doNotMatch = 'ERROR';
            } else {
                $scope.doNotMatch = null;
                Auth.changePassword($scope.password).then(function (response) {
                    $scope.error = null;
                    $scope.success = 'OK';
                }).catch(function (error) {
                    $scope.success = null;
                    $scope.error = 'ERROR';
                });
            }
        };
    });
