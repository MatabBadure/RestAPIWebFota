'use strict';

angular.module('hillromvestApp')
    .factory('AuthServerProvider', function loginService($http, localStorageService, Base64) {
        return {
            login: function(credentials) {
                var data = {
                  'username' : credentials.username,
                  'password' : credentials.password
                };
                return $http.post('api/authenticate', data, {
                    headers: {
                        "Content-Type": "application/json",
                        "Accept": "application/json"
                    }
                }).success(function (response) {
                    localStorageService.set('token', response);
                    return response;
                });
            },
            logout: function() {
                //Stateless API : No server logout
                localStorageService.clearAll();
            },
            getToken: function () {
                return localStorageService.get('token');
            },
            hasValidToken: function () {
                var token = this.getToken();
                return token && token.expires && token.expires > new Date().getTime();
            },

            submitPassword: function (data) {
                return $http.post('api/', data, {
                    headers: {
                        "Content-Type": "application/json",
                        "Accept": "application/json"
                    }
                }).success(function (response) {
                    localStorageService.set('token', response);
                    return response;
                });
            },

            /*Temp Service Call From angular*/
            captcha: function (captchaData){
                var data = {
                    'secret': '6LfwMAkTAAAAAHnNpBlH7fEixBPQBqLffYfArQ0E',
                    'response': captchaData
                };

                 return $http.post('https://www.google.com/recaptcha/api/siteverify', data).
                   success(function(response) {
                	   return response;
                   });
            }
        };
    });
