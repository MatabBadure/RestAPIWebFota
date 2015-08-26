'use strict';

angular.module('hillromvestApp')
    .factory('Auth', function Auth($rootScope, $state, $q, $translate, Principal, AuthServerProvider, Account, Register, Activate, Password, PasswordResetInit, PasswordResetFinish, StorageService) {
        return {
            login: function (credentials, callback) {
                var cb = callback || angular.noop;
                var deferred = $q.defer();

                AuthServerProvider.login(credentials).then(function (data) {
                	localStorage.setItem('token', data.data.id);
                    Principal.identity(true).then(function(account) {

                        localStorage.setItem('role', account.roles[0]);
                        $translate.use(account.langKey);
                        $translate.refresh();
                        deferred.resolve(data);
                    });
                    return cb(data);
                }).catch(function (err) {
                    StorageService.remove('token');
                    deferred.reject(err);
                    return cb(err);
                }.bind(this));
                return deferred.promise;
            },

            signOut: function (newPassword, callback) {
                var cb = callback || angular.noop;
                var deferred = $q.defer();
                AuthServerProvider.signOut().then(function (data) {
                    deferred.resolve(data);
                    return cb(data);
                }).catch(function (err) {
                    deferred.reject(err);
                    return cb(err);
                });
                return deferred.promise;
            },

            logout: function () {
                localStorage.clear();
                AuthServerProvider.logout();
                Principal.authenticate(null);
            },

            authorize: function(force) {
                return Principal.identity(force)
                    .then(function() {
                        var isAuthenticated = Principal.isAuthenticated();

                        if ($rootScope.toState.data.roles && $rootScope.toState.data.roles.length > 0 && !Principal.isInAnyRole($rootScope.toState.data.roles)) {
                            if (isAuthenticated) {
                                // user is signed in but not authorized for desired state
                                // $state.go('accessdenied');
                                $state.go('pageUnderConstruction');
                            }
                            else {
                                // user is not authenticated. stow the state they wanted before you
                                // send them to the signin state, so you can return them when you're done
                                $rootScope.returnToState = $rootScope.toState;
                                $rootScope.returnToStateParams = $rootScope.toStateParams;

                                // now, send them to the signin state so they can log in
                                $state.go('login');
                            }
                        }

                        if(isAuthenticated && $rootScope.toState.url == "/login"){
                            $state.go('patientUser');
                        }
                    });
            },
            createAccount: function (account, callback) {
                var cb = callback || angular.noop;

                return Register.save(account,
                    function () {
                        return cb(account);
                    },
                    function (err) {
                        this.logout();
                        return cb(err);
                    }.bind(this)).$promise;
            },

            updateAccount: function (account, callback) {
                var cb = callback || angular.noop;

                return Account.save(account,
                    function () {
                        return cb(account);
                    },
                    function (err) {
                        return cb(err);
                    }.bind(this)).$promise;
            },

            activateAccount: function (key, callback) {
                var cb = callback || angular.noop;

                return Activate.get(key,
                    function (response) {
                        return cb(response);
                    },
                    function (err) {
                        return cb(err);
                    }.bind(this)).$promise;
            },

            changePassword: function (newPassword, callback) {
                var cb = callback || angular.noop;
                var data = {"password":newPassword};
                var deferred = $q.defer();
                Password.changePassword(data).then(function (data) {
                    deferred.resolve(data);
                    return cb(data);
                }).catch(function (err) {
                    deferred.reject(err);
                    return cb(err);
                });
                return deferred.promise;
            },

            resetPasswordInit: function (mail, callback) {
                var cb = callback || angular.noop;
                var data ={"email":mail};
                return PasswordResetInit.save(data, function() {
                    return cb();
                }, function (err) {
                    return cb(err);
                }).$promise;
            },


            resetPasswordFinish: function(key, data, callback) {
                var cb = callback || angular.noop;
                var data = {"questionId":data.question.id,
                            "answer": data.answer,
                            "password":data.password
                           };
                return PasswordResetFinish.resetPassFinish(key).save(data, function () {
                    return cb();
                }, function (err) {
                    return cb(err);
                }).$promise;
            },

            submitPassword : function(obj, callback){
                var deferred = $q.defer();
                var cb = callback || angular.noop;
                AuthServerProvider.submitPassword(obj).then(function (data) {
                    deferred.resolve(data);
                    return cb(data);
                }).catch(function (err) {
                    this.logout();
                    deferred.reject(err);
                    return cb(err);
                }.bind(this));
                return deferred.promise;
            },

            configurePassword : function(obj, callback){
                var deferred = $q.defer();
                var cb = callback || angular.noop;
                AuthServerProvider.configurePassword(obj).then(function (data) {
                    deferred.resolve(data);
                    return cb(data);
                }).catch(function (err) {
                    this.logout();
                    deferred.reject(err);
                    return cb(err);
                }.bind(this));
                return deferred.promise;
            },

             getSecurityQuestions : function(callback){
                var deferred = $q.defer();
                var cb = callback || angular.noop;
                AuthServerProvider.getSecurityQuestions().then(function (data) {
                    deferred.resolve(data);
                    return cb(data);
                }).catch(function (err) {
                    deferred.reject(err);
                    return cb(err);
                }.bind(this));
                return deferred.promise;
            },


            /*Temp Fix from  angular*/
            captcha: function(captchaData, callback){
                var deferred = $q.defer();
                var cb = callback || angular.noop;
                AuthServerProvider.captcha(captchaData).then(function (data) {
                    deferred.resolve(data);
                    return cb(data);
                }).catch(function (err) {
                    //this.logout();
                    deferred.reject(err);
                    return cb(err);
                }.bind(this));
                return deferred.promise;
            }
        };
    });
