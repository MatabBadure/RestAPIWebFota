'use strict';

angular.module('hillromvestApp')
    .controller('ClinicsController', function ($rootScope, $scope, $state, $timeout, Auth) {
    $scope.clinic = {};
    $scope.clinicStatus ={
        'isCreate':true,
        'isClinicCreated':false,
        'isClinicDeleted':false
    }
    $scope.selectedClinic = function(clinic) {
        $scope.clinicStatus ={
        'isCreate':false,
        'isClinicCreated':false,
        'isClinicDeleted':false
    }
        $scope.clinic = clinic;
    };

    });


