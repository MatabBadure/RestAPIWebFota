'use strict';

angular.module('hillromvestApp')
    .config(function($stateProvider) {
        $stateProvider
            .state('entity', {
                abstract: true,
                parent: 'site'
              })
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

            .state('patientClinics', {
                parent: 'patientUser',
                url: '/{patientId}/clinic-info',
                data: {
                    roles: ['ADMIN'],
                    pageTitle: 'patient.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/modules/admin/patient/views/patient-view/view.html',
                        controller: 'patientsController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('patient-user');
                        return $translate.refresh();
                    }],
                    authorize: ['Auth',
                        function(Auth) {
                            return Auth.authorize(false);
                        }
                    ]
                }
            })
            .state('patientProtocol', {
                parent: 'patientUser',
                url: '/{patientId}/protocol-device',
                data: {
                    roles: ['ADMIN'],
                    pageTitle: 'patient.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/modules/admin/patient/directives/patient-info/device-protocol/list.html',
                        controller: 'patientsController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('patient-user');
                        return $translate.refresh();
                    }],
                    authorize: ['Auth',
                        function(Auth) {
                            return Auth.authorize(false);
                        }
                    ]
                }
            })
            .state('patientCraegiver', {
                parent: 'patientUser',
                url: '/{patientId}/caregiver',
                data: {
                    roles: ['ADMIN'],
                    pageTitle: 'patient.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/modules/admin/patient/directives/patient-info/caregiver/list.html',
                        controller: 'patientsController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('patient-user');
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
                url: '/hillRomUsers',
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
            .state('hillRomUserNew', {
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
            .state('hillRomUserEdit', {
                parent: 'hillRomUser',
                url: '/{userId}',
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
                url: '/hcpUsers',
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
            .state('hcpNew', {
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
            .state('hcpEdit', {
                parent: 'hcpUser',
                url: '/{doctorId}',
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

            .state('patient', {
                parent: 'entity',
                url: '/patient',
                abstract: true,
            })
});