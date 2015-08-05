'use strict';

angular.module('hillromvestApp')
  .config(function($stateProvider) {
    $stateProvider
      .state('admin', {
        parent: 'entity',
        url: '/admin',
        abstract: true,
      })
      .state('patientUser', {
        parent: 'admin',
        url: '/patients',
        data: {
          roles: ['ADMIN'],
          pageTitle: 'patient.title'
        },
        views: {
          'content@': {
            templateUrl: 'scripts/app/modules/admin/patient/views/list/view.html',
            controller: 'patientsController'
          }
        },
        resolve: {
          translatePartialLoader: ['$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
            $translatePartialLoader.addPart('patient');
            return $translate.refresh();
          }],
          authorize: ['Auth',
            function(Auth) {
              return Auth.authorize(false);
            }
          ]
        }
      })
      .state('patientNew', {
        parent: 'patientUser',
        url: '/new',
        data: {
          roles: ['ADMIN'],
          pageTitle: 'patient.title'
        },
        views: {
          'content@': {
            templateUrl: 'scripts/app/modules/admin/patient/views/create-edit/view.html',
            controller: 'patientsController'
          }
        },
        resolve: {
          translatePartialLoader: ['$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
            $translatePartialLoader.addPart('patient');
            return $translate.refresh();
          }],
          authorize: ['Auth',
            function(Auth) {
              return Auth.authorize(false);
            }
          ]
        }
      })
      .state('patientEdit', {
        parent: 'patientUser',
        url: '/{patientId}',
        data: {
          roles: ['ADMIN'],
          pageTitle: 'patient.title'
        },
        views: {
          'content@': {
            templateUrl: 'scripts/app/modules/admin/patient/views/create-edit/view.html',
            controller: 'patientsController'
          }
        },
        resolve: {
          translatePartialLoader: ['$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
            $translatePartialLoader.addPart('patient');
            return $translate.refresh();
          }],
          authorize: ['Auth',
            function(Auth) {
              return Auth.authorize(false);
            }
          ]
        }
      })
    });