'use strict';

angular.module('hillromvestApp')
    .controller('PatientVestDeviceRawLogController', function ($scope, PatientVestDeviceRawLog, ParseLinks) {
        $scope.patientVestDeviceRawLogs = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            PatientVestDeviceRawLog.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.patientVestDeviceRawLogs = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            PatientVestDeviceRawLog.get({id: id}, function(result) {
                $scope.patientVestDeviceRawLog = result;
                $('#savePatientVestDeviceRawLogModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.patientVestDeviceRawLog.id != null) {
                PatientVestDeviceRawLog.update($scope.patientVestDeviceRawLog,
                    function () {
                        $scope.refresh();
                    });
            } else {
                PatientVestDeviceRawLog.save($scope.patientVestDeviceRawLog,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            PatientVestDeviceRawLog.get({id: id}, function(result) {
                $scope.patientVestDeviceRawLog = result;
                $('#deletePatientVestDeviceRawLogConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            PatientVestDeviceRawLog.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deletePatientVestDeviceRawLogConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#savePatientVestDeviceRawLogModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.patientVestDeviceRawLog = {deviceModelType: null, deviceData: null, deviceSerialNo: null, deviceType: null, hubId: null, airInterfaceType: null, customerName: null, timeZone: null, spReceiveTime: null, hubReceiveTime: null, deviceAddress: null, hubReceiveTimeOffset: null, cucVersion: null, customerId: null, rawMessage: null, rawHexaData: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
