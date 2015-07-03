'use strict';

angular.module('hillromvestApp')
    .controller('PatientInfoDetailController', function ($scope, $stateParams, PatientInfo, User) {
        $scope.patientInfo = {};
        $scope.load = function (id) {
            PatientInfo.get({id: id}, function(result) {
              $scope.patientInfo = result;
            });
        };
        $scope.load($stateParams.id);
    });
