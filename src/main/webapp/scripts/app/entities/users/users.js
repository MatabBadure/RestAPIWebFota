'use strict';

angular.module('hillromvestApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('user', {
                parent: 'entity',
                url: '/user',
                data: {
                    roles: ['ADMIN'],
                    pageTitle: 'user.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/users/users.html',
                        controller: 'UsersController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('hillRomUser');
                        return $translate.refresh();
                    }]
                }
            });
    });
