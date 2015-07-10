'use strict';

angular.module('hillromvestApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('userExtension', {
                parent: 'entity',
                url: '/userExtension',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'hillromvestApp.userExtension.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/userExtension/userExtensions.html',
                        controller: 'UserExtensionController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('userExtension');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('userExtensionDetail', {
                parent: 'entity',
                url: '/userExtension/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'hillromvestApp.userExtension.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/userExtension/userExtension-detail.html',
                        controller: 'UserExtensionDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('userExtension');
                        return $translate.refresh();
                    }]
                }
            });
    });
