'use strict';

angular.module('hillromvestApp')
    .controller('PATIENT_VEST_DEVICE_RAW_LOGSDetailController', function ($scope, $stateParams, PATIENT_VEST_DEVICE_RAW_LOGS) {
        $scope.pATIENT_VEST_DEVICE_RAW_LOGS = {};
        $scope.load = function (id) {
            PATIENT_VEST_DEVICE_RAW_LOGS.get({id: id}, function(result) {
              $scope.pATIENT_VEST_DEVICE_RAW_LOGS = result;
            });
        };
        $scope.load($stateParams.id);
    });
