'use strict';

angular.module('hillromvestApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('docs', {
                parent: 'admin',
                url: '/docs',
                data: {
                    roles: ['ADMIN'],
                    pageTitle: 'global.menu.admin.apidocs'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/admin/docs/docs.html'
                    }
                }
            });
    });
