'use strict';

angular.module('hillromvestApp')
    .factory('Password', function ($resource) {
        return $resource('api/account/change_password', {}, {
        });
    });

angular.module('hillromvestApp')
    .factory('PasswordResetInit', function ($http) {
        /*return $resource('api/account/reset_password/init', {}, {
         * 
        })*/
    	 return {
             resetPassword: function(mail) {
                 var data = {
                   'email' : mail
                 };
                 return $http.post('api/account/reset_password/init', data, {
                     headers: {
                         "Content-Type": "application/json",
                         "Accept": "application/json"
                     }
                 });/*.success(function (response) {
                     return response;
                 });*/
             }
    	 }
    });

angular.module('hillromvestApp')
    .factory('PasswordResetFinish', function ($resource) {
        return $resource('api/account/reset_password/finish', {}, {
        })
    });
