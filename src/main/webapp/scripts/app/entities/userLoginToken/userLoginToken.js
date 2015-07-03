'use strict';

angular.module('hillromvestApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('userLoginToken', {
                parent: 'entity',
                url: '/userLoginToken',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'hillromvestApp.userLoginToken.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/userLoginToken/userLoginTokens.html',
                        controller: 'UserLoginTokenController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('userLoginToken');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('userLoginTokenDetail', {
                parent: 'entity',
                url: '/userLoginToken/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'hillromvestApp.userLoginToken.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/userLoginToken/userLoginToken-detail.html',
                        controller: 'UserLoginTokenDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('userLoginToken');
                        return $translate.refresh();
                    }]
                }
            });
    });
