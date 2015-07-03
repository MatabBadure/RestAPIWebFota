'use strict';

angular.module('hillromvestApp')
    .controller('PatientInfoController', function ($scope, PatientInfo, User, ParseLinks) {
        $scope.patientInfos = [];
        $scope.users = User.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            PatientInfo.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.patientInfos = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            PatientInfo.get({id: id}, function(result) {
                $scope.patientInfo = result;
                $('#savePatientInfoModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.patientInfo.id != null) {
                PatientInfo.update($scope.patientInfo,
                    function () {
                        $scope.refresh();
                    });
            } else {
                PatientInfo.save($scope.patientInfo,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            PatientInfo.get({id: id}, function(result) {
                $scope.patientInfo = result;
                $('#deletePatientInfoConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            PatientInfo.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deletePatientInfoConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#savePatientInfoModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.patientInfo = {mrn: null, hillromId: null, hubId: null, serialNumber: null, bluetoothId: null, title: null, firstName: null, middleName: null, lastName: null, dob: null, email: null, webLoginCreated: null, isDeleted: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
