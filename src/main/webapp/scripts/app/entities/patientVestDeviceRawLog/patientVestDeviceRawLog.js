'use strict';

angular.module('hillromvestApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('patientVestDeviceRawLog', {
                parent: 'entity',
                url: '/patientVestDeviceRawLog',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'hillromvestApp.patientVestDeviceRawLog.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/patientVestDeviceRawLog/patientVestDeviceRawLogs.html',
                        controller: 'PatientVestDeviceRawLogController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('patientVestDeviceRawLog');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('patientVestDeviceRawLogDetail', {
                parent: 'entity',
                url: '/patientVestDeviceRawLog/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'hillromvestApp.patientVestDeviceRawLog.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/patientVestDeviceRawLog/patientVestDeviceRawLog-detail.html',
                        controller: 'PatientVestDeviceRawLogDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('patientVestDeviceRawLog');
                        return $translate.refresh();
                    }]
                }
            });
    });
