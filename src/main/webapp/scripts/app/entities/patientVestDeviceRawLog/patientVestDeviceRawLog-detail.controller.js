'use strict';

angular.module('hillromvestApp')
    .controller('PatientVestDeviceRawLogDetailController', function ($scope, $stateParams, PatientVestDeviceRawLog) {
        $scope.patientVestDeviceRawLog = {};
        $scope.load = function (id) {
            PatientVestDeviceRawLog.get({id: id}, function(result) {
              $scope.patientVestDeviceRawLog = result;
            });
        };
        $scope.load($stateParams.id);
    });
