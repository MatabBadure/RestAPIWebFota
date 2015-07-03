'use strict';

angular.module('hillromvestApp')
    .config(function ($stateProvider, reCAPTCHAProvider) {

        reCAPTCHAProvider.setPublicKey('6LfwMAkTAAAAAG0CeUbZljDztBq8l8iovStQeqHM');
        reCAPTCHAProvider.setOptions({
          theme: 'clean'
        });

        $stateProvider
            .state('login', {
                parent: 'account',
                url: '/login',
                data: {
                    roles: [],
                    pageTitle: 'login.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/account/login/login.html',
                        controller: 'LoginController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('login');
                        return $translate.refresh();
                    }]
                }
            });
    });
