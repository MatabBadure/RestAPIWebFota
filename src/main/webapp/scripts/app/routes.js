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
        url: '/patient',
        abstract: true,
      })
      .state('patientList', {
        parent: 'patientUser',
        url: '/list',
        data: {
          roles: ['ADMIN'],
          pageTitle: 'patient.title'
        },
        views: {
          'content@': {
            templateUrl: 'scripts/app/modules/admin/patient/views/view.html',
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
      .state('hillRomUser', {
        parent: 'admin',
        url: '/hillromuser',
        abstract: true,
      })
      .state('userList', {
        parent: 'hillRomUser',
        url: '/list',
        data: {
          roles: ['ADMIN'],
          pageTitle: 'patient.title'
        },
        views: {
          'content@': {
            templateUrl: 'scripts/app/modules/admin/hill-rom-user/views/list/view.html',
            controller: 'UsersController'
          }
        },
        resolve: {
          translatePartialLoader: ['$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
            $translatePartialLoader.addPart('hillRomUser');
            return $translate.refresh();
          }],
          authorize: ['Auth',
            function(Auth) {
              return Auth.authorize(false);
            }
          ]
        }
      })
      .state('userCreate', {
        parent: 'hillRomUser',
        url: '/new',
        data: {
          roles: ['ADMIN'],
          pageTitle: 'patient.title'
        },
        views: {
          'content@': {
            templateUrl: 'scripts/app/modules/admin/hill-rom-user/views/create-edit/view.html',
            controller: 'UsersController'
          }
        },
        resolve: {
          translatePartialLoader: ['$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
            $translatePartialLoader.addPart('hillRomUser');
            return $translate.refresh();
          }],
          authorize: ['Auth',
            function(Auth) {
              return Auth.authorize(false);
            }
          ]
        }
      })
      .state('userEdit', {
        parent: 'hillRomUser',
        url: '/{userId}/edit',
        data: {
          roles: ['ADMIN'],
          pageTitle: 'patient.title'
        },
        views: {
          'content@': {
            templateUrl: 'scripts/app/modules/admin/hill-rom-user/views/create-edit/view.html',
            controller: 'UsersController'
          }
        },
        resolve: {
          translatePartialLoader: ['$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
            $translatePartialLoader.addPart('hillRomUser');
            return $translate.refresh();
          }],
          authorize: ['Auth',
            function(Auth) {
              return Auth.authorize(false);
            }
          ]
        }
      })
      .state('hcpUser', {
        parent: 'admin',
        url: '/hcp',
        abstract: true,
      })
      .state('hcpList', {
        parent: 'hcpUser',
        url: '/list',
        data: {
          roles: ['ADMIN'],
          pageTitle: 'patient.title'
        },
        views: {
          'content@': {
            templateUrl: 'scripts/app/modules/admin/hcp/views/list/view.html',
            controller: 'DoctorsController'
          }
        },
        resolve: {
          translatePartialLoader: ['$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
            $translatePartialLoader.addPart('doctor');
            return $translate.refresh();
          }],
          authorize: ['Auth',
            function(Auth) {
              return Auth.authorize(false);
            }
          ]
        }
      })
      .state('createHCP', {
        parent: 'hcpUser',
        url: '/new',
        data: {
          roles: ['ADMIN'],
          pageTitle: 'patient.title'
        },
        views: {
          'content@': {
            templateUrl: 'scripts/app/modules/admin/hcp/views/create-edit/view.html',
            controller: 'DoctorsController'
          }
        },
        resolve: {
          translatePartialLoader: ['$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
            $translatePartialLoader.addPart('doctor');
            return $translate.refresh();
          }],
          authorize: ['Auth',
            function(Auth) {
              return Auth.authorize(false);
            }
          ]
        }
      })
      .state('editHCP', {
        parent: 'hcpUser',
        url: '/{doctorId}/edit',
        data: {
          roles: ['ADMIN'],
          pageTitle: 'patient.title'
        },
        views: {
          'content@': {
            templateUrl: 'scripts/app/modules/admin/hcp/views/create-edit/view.html',
            controller: 'DoctorsController'
          }
        },
        resolve: {
          translatePartialLoader: ['$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
            $translatePartialLoader.addPart('doctor');
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
/*
      .state('patient', {
        parent: 'entity',
        url: '/patient',
        abstract: true,
      })
      .state('hcp', {
        parent: 'entity',
        url: '/hcp',
        abstract: true,
      })
      .state('caregiver', {
        parent: 'entity',
        url:'/caregiver',
        abstract: true,
      })
      .state('acct-services', {
        parent: 'entity',
        url:'/acct-services',
        abstract: true,
      })
      .state('associates', {
        parent: 'entity',
        url:'/associates',
        abstract: true,
      })
      .state('clinic-admin', {
        parent: 'entity',
        url:'/clinic-admin',
        abstract: true,
      })*/