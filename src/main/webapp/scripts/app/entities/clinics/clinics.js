'use strict';

angular.module('hillromvestApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('clinic', {
                parent: 'entity',
                url: '/clinic',
                data: {
                    roles: ['ADMIN'],
                    pageTitle: 'clinic.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/clinics/clinics.html',
                        controller: 'ClinicsController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('clinic');
                        return $translate.refresh();
                    }]

                }
            });
    });
