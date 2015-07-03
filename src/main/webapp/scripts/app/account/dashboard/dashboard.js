'use strict';

angular.module('hillromvestApp')
    .config(function ($stateProvider, reCAPTCHAProvider) {
        $stateProvider
            .state('dashboard', {
                parent: 'account',
                url: '/dashboard',
                data: {
                    roles: [],
                    pageTitle: 'login.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/account/dashboard/dashboard.html',
                        controller: 'DashboardController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        // $translatePartialLoader.addPart('dashboard');
                        return $translate.refresh();
                    }]
                }
            });
    });
