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

      .state('clinicUser', {
        parent: 'admin',
        url: '/clinics',
        data: {
          roles: ['ADMIN'],
          pageTitle: 'clinic.title'
        },
        views: {
          'content@': {
            templateUrl: 'scripts/app/modules/admin/clinic/views/list/view.html',
            controller: 'clinicsController'
          }
        },
        resolve: {
          translatePartialLoader: ['$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
            $translatePartialLoader.addPart('clinic');
            return $translate.refresh();
          }],
          authorize: ['Auth',
            function(Auth) {
              return Auth.authorize(false);
            }
          ]
        }
      })

      .state('clinicNew', {
        parent: 'clinicUser',
        url: '/new',
        data: {
          roles: ['ADMIN'],
          pageTitle: 'clinic.title'
        },
        views: {
          'content@': {
            templateUrl: 'scripts/app/modules/admin/clinic/views/create-edit/view.html',
            controller: 'clinicsController'
          }
        },
        resolve: {
          translatePartialLoader: ['$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
            $translatePartialLoader.addPart('clinic');
            return $translate.refresh();
          }],
          authorize: ['Auth',
            function(Auth) {
              return Auth.authorize(false);
            }
          ]
        }
      })

      .state('clinicEdit', {
        parent: 'clinicUser',
        url: '/{clinicId}',
        data: {
          roles: ['ADMIN'],
          pageTitle: 'clinic.title'
        },
        views: {
          'content@': {
            templateUrl: 'scripts/app/modules/admin/clinic/views/create-edit/view.html',
            controller: 'clinicsController'
          }
        },
        resolve: {
          translatePartialLoader: ['$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
            $translatePartialLoader.addPart('clinic');
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