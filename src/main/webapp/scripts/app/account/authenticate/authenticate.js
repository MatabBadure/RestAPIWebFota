'use strict';

angular.module('hillromvestApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('authenticate', {
                parent: 'account',
                url: '/authenticate?key',
                data: {
                    roles: [],
                    pageTitle: 'authenticate.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/account/authenticate/authenticate.html',
                        controller: 'AuthenticateController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('authenticate');
                        return $translate.refresh();
                    }]
                }
            });
    });

