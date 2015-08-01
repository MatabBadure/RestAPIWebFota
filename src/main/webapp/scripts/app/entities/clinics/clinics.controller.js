    'use strict';

    angular.module('hillromvestApp')
    .controller('ClinicsController', function ($rootScope, $scope, $state, $timeout, Auth) {
        $scope.clinic = {};
        $scope.clinicStatus =
        {
            'role':localStorage.getItem('role'),
            'editMode':false,
            'isCreate':false,
            'isMessage':false,
            'message': ''
        }
        $scope.selectedClinic = function(clinic) {
            $scope.clinicStatus.editMode = true;
            $scope.clinicStatus.isCreate = false;
            $scope.clinic = clinic;
        };
        $scope.createClinic = function(){
            $scope.clinicStatus.isCreate = true;
            $scope.clinicStatus.isMessage = false;
        };

        $scope.onSuccess = function () {
            $scope.$broadcast('resetList', {});
        };

    });


