'use strict';

angular.module('hillromvestApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('health', {
                parent: 'admin',
                url: '/health',
                data: {
                    roles: ['ADMIN'],
                    pageTitle: 'health.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/admin/health/health.html',
                        controller: 'HealthController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('health');
                        return $translate.refresh();
                    }]
                }
            });
    });
