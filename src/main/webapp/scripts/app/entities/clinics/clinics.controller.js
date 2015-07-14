'use strict';

angular.module('hillromvestApp')
    .controller('ClinicsController', function ($rootScope, $scope, $state, $timeout, Auth) {
    $scope.clinic = {};
    $scope.clinicStatus ={
        'isCreate':true,
        'isMessage':false
    }
    $scope.selectedClinic = function(clinic) {
        $scope.clinicStatus ={
        'isCreate':false,
        'isMessage':false
    }
        $scope.clinic = clinic;
    };

    });


