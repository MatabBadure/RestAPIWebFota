'use strict';

angular.module('hillromvestApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('patientInfo', {
                parent: 'entity',
                url: '/patientInfo',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'hillromvestApp.patientInfo.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/patientInfo/patientInfos.html',
                        controller: 'PatientInfoController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('patientInfo');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('patientInfoDetail', {
                parent: 'entity',
                url: '/patientInfo/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'hillromvestApp.patientInfo.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/patientInfo/patientInfo-detail.html',
                        controller: 'PatientInfoDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('patientInfo');
                        return $translate.refresh();
                    }]
                }
            });
    });
