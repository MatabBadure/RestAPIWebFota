'use strict';

angular.module('hillromvestApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('doctor', {
                parent: 'entity',
                url: '/doctor',
                data: {
                    roles: ['ADMIN'],
                    pageTitle: 'doctor.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/doctors/doctors.html',
                        controller: 'DoctorsController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('doctor');
                        return $translate.refresh();
                    }]
                }
            });
    });
