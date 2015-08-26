'use strict';

angular.module('hillromvestApp')
    .factory('Password', function ($http,localStorageService, headerService) {
        /*return $resource('api/account/change_password', {}, {
            headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json',
                        'x-auth-token': localStorage.getItem('token')
                    }
        });*/
    return {
            changePassword: function(data) {

                return $http.post('api/account/change_password', data, {
                    headers: {
                        "Content-Type": "application/json",
                        "Accept": "application/json",
                        'x-auth-token': localStorage.getItem('token')
                    }
                }).success(function (data, status, headers, config) {
                    return {'response': data, 'status': status, 'headers' : headers, 'config' : config};
                });
            },

            updatePassword: function(id, data){
                return $http.put('/api/user/'+id+'/update_password', data, {
                    headers: headerService.getHeader()
                }).success(function (data, status, headers, config) {
                    return {'response': data, 'status': status, 'headers' : headers, 'config' : config};
                });
            }
        }
    });


angular.module('hillromvestApp')
    .factory('PasswordResetInit', function ($resource) {
    	return $resource('api/account/reset_password/init', {}, {
    	 })
    });

angular.module('hillromvestApp')
    .factory('PasswordResetFinish', function ($resource) {
        return{
        	resetPassFinish : function(key){
        		return  $resource('api/account/reset_password/finish?key='+key, {}, {
                });
        	}

        }
    });
