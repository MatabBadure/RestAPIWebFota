'use strict';

angular.module('hillromvestApp')
    .controller('UserLoginTokenController', function ($scope, UserLoginToken, User) {
        $scope.userLoginTokens = [];
        $scope.users = User.query();
        $scope.loadAll = function() {
            UserLoginToken.query(function(result) {
               $scope.userLoginTokens = result;
            });
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            UserLoginToken.get({id: id}, function(result) {
                $scope.userLoginToken = result;
                $('#saveUserLoginTokenModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.userLoginToken.id != null) {
                UserLoginToken.update($scope.userLoginToken,
                    function () {
                        $scope.refresh();
                    });
            } else {
                UserLoginToken.save($scope.userLoginToken,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            UserLoginToken.get({id: id}, function(result) {
                $scope.userLoginToken = result;
                $('#deleteUserLoginTokenConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            UserLoginToken.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteUserLoginTokenConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveUserLoginTokenModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.userLoginToken = {createdTime: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
